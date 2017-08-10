package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.BotDao;
import me.exrates.model.*;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.*;
import me.exrates.service.*;
import me.exrates.service.exception.InsufficientCostsForAcceptionException;
import me.exrates.service.exception.OrderAcceptionException;
import me.exrates.service.job.bot.BotCreateOrderJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
@PropertySource("classpath:/bot_trader.properties")
public class BotServiceImpl implements BotService {

    private @Value("${bot.error.noMoney.email}") String errorEmail;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private BotDao botDao;

    @Autowired
    private Scheduler botOrderCreationScheduler;




    private final static ExecutorService botAcceptExecutors = Executors.newCachedThreadPool();


    @Override
    @Transactional
    public void acceptAfterDelay(ExOrder exOrder) {
        if (checkNeedToAccept(exOrder)) {
            retrieveBotFromDB().ifPresent(botTrader -> {
                if (botTrader.getIsEnabled()) {
                    botAcceptExecutors.execute(() -> {
                        try {
                            Thread.sleep(1000 * botTrader.getAcceptDelayInSeconds());
                            orderService.acceptOrder(botTrader.getUserId(), exOrder.getId(), Locale.ENGLISH);
                        } catch (InsufficientCostsForAcceptionException e) {
                            Email email = new Email();
                            email.setMessage("Insufficient costs on bot account");
                            email.setSubject(e.getMessage());
                            email.setTo(errorEmail);
                            sendMailService.sendInfoMail(email);
                            log.warn(e.getMessage());
                        } catch (OrderAcceptionException e) {
                            log.warn(e.getMessage());
                        } catch (InterruptedException e) {
                            log.error(e);
                        }
                    });
                }
            });
        }
    }

    private boolean checkNeedToAccept(ExOrder exOrder) {
        UserRoleSettings userRoleSettings = userRoleService.retrieveSettingsForRole(
                userService.getUserRoleFromDB(exOrder.getUserId()).getRole());

        return exOrder.getOrderBaseType() == OrderBaseType.LIMIT && userRoleSettings.isBotAcceptionAllowed();

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BotTrader> retrieveBotFromDB() {
        return botDao.retrieveBotTrader();
    }

    @Override
    @Transactional
    public void createBot(String nickname, String email, String password) {
        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(UserRole.BOT_TRADER);
        user.setStatus(UserStatus.ACTIVE);

        userService.createUserByAdmin(user);
        Integer userId = userService.getIdByEmail(email);
        referralService.bindChildAndParent(userId, userService.getCommonReferralRoot().getId());
        botDao.createBot(userId);
    }

    @Override
    @Transactional
    public void updateBot(BotTrader botTrader) {
        botDao.updateBot(botTrader);

    }


    @Override
    @Transactional
    public void runOrderCreation(Integer currencyPairId, OrderType orderType) {
        retrieveBotFromDB().ifPresent(bot -> {
            CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
            runOrderCreationSequence(currencyPair, orderType, bot);
        });
    }

    private void runOrderCreationSequence(CurrencyPair currencyPair, OrderType orderType, BotTrader botTrader) {
        botDao.retrieveBotSettingsForCurrencyPairAndOrderType(botTrader.getId(), currencyPair.getId(), orderType.getType()).ifPresent(settings -> {
            String userEmail = userService.getEmailById(botTrader.getUserId());
            OperationType operationType = OperationType.valueOf(orderType.name());
            PriceGrowthDirection initialDirection = settings.getDirection();
            BigDecimal lastPrice = orderService.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair, operationType).orElse(settings.getMinPrice());
            for(int i = 0; i < settings.getBotLaunchSettings().getQuantityPerSequence(); i++) {
                try {
                    Thread.sleep(settings.getBotLaunchSettings().getCreateTimeoutInSeconds());
                    BigDecimal newPrice = settings.nextPrice(lastPrice);
                    prepareAndSaveOrder(currencyPair, operationType, userEmail, settings.getRandomizedAmount(), newPrice);
                    lastPrice = newPrice;
                } catch (Exception e) {
                    log.error(e);
                }
            }
            if (settings.getDirection() != initialDirection) {
                botDao.updatePriceGrowthDirection(settings.getId(), settings.getDirection());
            }
        });
    }


    private synchronized void prepareAndSaveOrder(CurrencyPair currencyPair, OperationType operationType, String userEmail, BigDecimal amount, BigDecimal rate) {
        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(currencyPair, operationType, userEmail, amount, rate);
        orderService.createOrder(orderCreateDto, OrderActionEnum.CREATE);
    }


    @Override
    public void enableBotForCurrencyPair(CurrencyPair currencyPair) {
        retrieveBotFromDB().ifPresent(bot -> {
            botDao.setEnabledForCurrencyPair(bot.getId(), currencyPair.getId(), true);
            JobDetail jobDetailBuy = JobBuilder.newJob(BotCreateOrderJob.class)
                    .withIdentity(String.format("job_%s_%s", currencyPair.getName(), OrderType.BUY.name()))
                    .usingJobData("currencyPairId", currencyPair.getId())
                    .usingJobData("orderType", OrderType.BUY.name())
                    .build();
            JobDetail jobDetailSell = JobBuilder.newJob(BotCreateOrderJob.class)
                    .withIdentity(String.format("job_%s_%s", currencyPair.getName(), OrderType.SELL.name()))
                    .usingJobData("currencyPairId", currencyPair.getId())
                    .usingJobData("orderType", OrderType.SELL.name())
                    .build();
            Trigger triggerBuy = TriggerBuilder.newTrigger()
                    .withIdentity(String.format("trigger_%s_%s", currencyPair.getName(), OrderType.BUY.name()))
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(60).repeatForever())
                    .build();
            Trigger triggerSell = TriggerBuilder.newTrigger()
                    .withIdentity(String.format("trigger_%s_%s", currencyPair.getName(), OrderType.SELL.name()))
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(60).repeatForever())
                    .build();
            try {
                botOrderCreationScheduler.scheduleJob(jobDetailBuy, triggerBuy);
                botOrderCreationScheduler.scheduleJob(jobDetailSell, triggerSell);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });

    }



}
