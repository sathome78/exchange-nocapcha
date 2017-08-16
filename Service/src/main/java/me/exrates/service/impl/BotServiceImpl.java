package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.BotDao;
import me.exrates.model.*;
import me.exrates.model.dto.BotTradingSettingsShortDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.*;
import me.exrates.service.*;
import me.exrates.service.exception.BotException;
import me.exrates.service.exception.InsufficientCostsForAcceptionException;
import me.exrates.service.exception.OrderAcceptionException;
import me.exrates.service.job.bot.BotCreateOrderJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
@PropertySource("classpath:/bot_trader.properties")
public class BotServiceImpl implements BotService {

    private @Value("${bot.error.noMoney.email}") String errorEmail;

    private static final String JOB_FORMAT = "job_%s_%s";

    private static final String TRIGGER_FORMAT = "trigger_%s_%s";

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

    @PostConstruct
    private void initBot() {
        retrieveBotFromDB().ifPresent(botTrader -> {
            botDao.retrieveLaunchSettingsForAllPairs(botTrader.getId(), true).forEach(settings -> {
                scheduleJobsForCurrencyPair(settings.getCurrencyPairId(), settings.getLaunchIntervalInMinutes());
            });

        });
    }

    @PreDestroy
    private void shutdownBot() {
        retrieveBotFromDB().ifPresent(botTrader -> {
            try {
                botOrderCreationScheduler.shutdown();
            } catch (SchedulerException e) {
                log.error(e);
            }

        });
    }


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
                    Thread.sleep(settings.getBotLaunchSettings().getCreateTimeoutInSeconds() * 1000);
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


    @Override
    @Transactional
    public synchronized void prepareAndSaveOrder(CurrencyPair currencyPair, OperationType operationType, String userEmail, BigDecimal amount, BigDecimal rate) {
        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(currencyPair, operationType, userEmail, amount, rate);
        orderService.createOrder(orderCreateDto, OrderActionEnum.CREATE);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableBotForCurrencyPair(Integer currencyPairId) {
        retrieveBotFromDB().ifPresent(bot -> {
            BotLaunchSettings launchSettings =  botDao.retrieveBotLaunchSettingsForCurrencyPair(bot.getId(), currencyPairId);
            if (!launchSettings.getIsEnabledForPair()) {
                botDao.setEnabledForCurrencyPair(bot.getId(), currencyPairId, true);
                scheduleJobsForCurrencyPair(currencyPairId, launchSettings.getLaunchIntervalInMinutes() );
            }

        });

    }

    private void scheduleJobsForCurrencyPair(Integer currencyPairId, int intervalInMinutes) {
        int intervalInSeconds = intervalInMinutes * 60;
        try {
            scheduleJobForCurrencyPairAndOrderType(currencyPairId, OrderType.SELL, intervalInSeconds);
            scheduleJobForCurrencyPairAndOrderType(currencyPairId, OrderType.BUY, intervalInSeconds);
        } catch (SchedulerException e) {
            log.error(e);
            throw new BotException(e.getMessage());
        }
    }

    private void scheduleJobForCurrencyPairAndOrderType(Integer currencyPairId, OrderType orderType, Integer intervalInSeconds) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(BotCreateOrderJob.class)
                .withIdentity(String.format(JOB_FORMAT, currencyPairId, orderType.name()))
                .usingJobData("currencyPairId", currencyPairId)
                .usingJobData("orderType", orderType.name())
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(String.format(TRIGGER_FORMAT, currencyPairId, orderType.name()))
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds).repeatForever())
                .build();
        botOrderCreationScheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    @Transactional
    public void disableBotForCurrencyPair(Integer currencyPairId) {
        retrieveBotFromDB().ifPresent(bot -> {
            botDao.setEnabledForCurrencyPair(bot.getId(), currencyPairId, false);
            try {
                botOrderCreationScheduler.deleteJob(JobKey.jobKey(String.format(JOB_FORMAT, currencyPairId, OrderType.SELL.name())));
                botOrderCreationScheduler.deleteJob(JobKey.jobKey(String.format(JOB_FORMAT, currencyPairId, OrderType.BUY.name())));
            } catch (SchedulerException e) {
                log.error(e);
                throw new BotException(e.getMessage());
            }

        });

    }

    @Override
    @Transactional(readOnly = true)
    public BotTradingSettingsShortDto retrieveTradingSettingsShort(int botLaunchSettingsId, int orderTypeId) {
        return botDao.retrieveTradingSettingsShort(botLaunchSettingsId, orderTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotLaunchSettings> retrieveLaunchSettings(int botId) {
        return botDao.retrieveLaunchSettingsForAllPairs(botId, null);
    }

    @Override
    public void toggleBotStatusForCurrencyPair(Integer currencyPairId, boolean status) {
        if (status) {
            enableBotForCurrencyPair(currencyPairId);
        } else {
            disableBotForCurrencyPair(currencyPairId);
        }
    }

    @Override
    public void updateLaunchSettings(BotLaunchSettings launchSettings) {
        botDao.updateLaunchSettings(launchSettings);

    }

    @Override
    public void updateTradingSettings(BotTradingSettingsShortDto tradingSettings) {
        botDao.updateTradingSettings(tradingSettings);

    }




}
