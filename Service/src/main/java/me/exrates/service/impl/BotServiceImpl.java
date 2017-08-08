package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.BotDao;
import me.exrates.model.*;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.*;
import me.exrates.service.exception.InsufficientCostsForAcceptionException;
import me.exrates.service.exception.OrderAcceptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private BotDao botDao;




    private final static ExecutorService botExecutors = Executors.newCachedThreadPool();




    @Override
    @Transactional
    public void acceptAfterDelay(ExOrder exOrder) {
        if (checkNeedToAccept(exOrder)) {
            retrieveBotFromDB().ifPresent(botTrader -> {
                if (botTrader.getIsEnabled()) {
                    botExecutors.execute(() -> {
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






}
