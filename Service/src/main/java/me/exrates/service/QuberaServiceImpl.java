package me.exrates.service;

import com.google.common.collect.Maps;
import me.exrates.dao.QuberaDao;
import me.exrates.model.Currency;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.QuberaUserData;
import me.exrates.model.User;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.constants.Constants;
import me.exrates.model.dto.AccountCreateDto;
import me.exrates.model.dto.AccountQuberaRequestDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentDto;
import me.exrates.model.dto.qubera.PaymentRequestDto;
import me.exrates.model.dto.qubera.QuberaPaymentInfoDto;
import me.exrates.model.dto.qubera.QuberaPaymentToMasterDto;
import me.exrates.model.dto.qubera.QuberaRequestDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgRefillException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.kyc.http.KycHttpClient;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@PropertySource("classpath:/merchants/qubera.properties")
@Conditional(MonolitConditional.class)
public class QuberaServiceImpl implements QuberaService {

    private static final Logger logger = LogManager.getLogger(QuberaServiceImpl.class);

    private final CurrencyService currencyService;
    private final GtagService gtagService;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final QuberaDao quberaDao;
    private final KycHttpClient kycHttpClient;
    private final UserService userService;
    private final StompMessenger stompMessenger;
    private final SendMailService sendMailService;
    private final CommissionService commissionService;

    private @Value("${qubera.threshold.length}")
    int thresholdLength;
    private @Value("${qubera.poolId}")
    int poolId;
    private @Value("${qubera.master.account}")
    String masterAccount;

    @Autowired
    public QuberaServiceImpl(CurrencyService currencyService,
                             GtagService gtagService,
                             MerchantService merchantService,
                             RefillService refillService,
                             QuberaDao quberaDao,
                             KycHttpClient kycHttpClient,
                             UserService userService,
                             StompMessenger stompMessenger,
                             SendMailService sendMailService,
                             CommissionService commissionService) {
        this.currencyService = currencyService;
        this.gtagService = gtagService;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.quberaDao = quberaDao;
        this.kycHttpClient = kycHttpClient;
        this.userService = userService;
        this.stompMessenger = stompMessenger;
        this.sendMailService = sendMailService;
        this.commissionService = commissionService;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(request.getAmount(), request.getCurrencyName());

        //create request to make payment to master account
        ResponsePaymentDto paymentToMaster = createPaymentToMaster(request.getUserEmail(), paymentRequestDto);

        Map<String, String> details = quberaDao.getUserDetailsForCurrency(request.getUserId(), request.getCurrencyId());
        Map<String, String> refillParams = Maps.newHashMap();
        String iban = details.getOrDefault("iban", "");
        String accountNumber = details.getOrDefault("accountNumber", "");
        refillParams.put("iban", iban);
        refillParams.put("currency", request.getCurrencyName());
        refillParams.put("accountNumber", accountNumber);
        refillParams.put("paymentAmount", paymentToMaster.getTransactionAmount().toPlainString());
        refillParams.put("paymentId", paymentToMaster.getPaymentId().toString());

        if (confirmPaymentToMaster(paymentToMaster.getPaymentId())) {
            try {
                processPayment(refillParams);
            } catch (RefillRequestAppropriateNotFoundException e) {
                logger.error("Some exception happens " + e.getMessage());
            }
        } else {
            logger.error("Payment not confirmed {}" + paymentToMaster.getPaymentId());
            throw new NgRefillException("Payment not confirmed {}" + paymentToMaster.getPaymentId());
        }
        return refillParams;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findByName("Qubera");
        int userId = quberaDao.findUserIdByAccountNumber(params.get("accountNumber"));

        String paymentAmount = params.getOrDefault("paymentAmount", "0");
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(0)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(new BigDecimal(paymentAmount))
                .address(StringUtils.EMPTY)
                .merchantTransactionId(params.get("paymentId"))
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createAndAutoAcceptRefillRequest(requestAcceptDto, userId);
        params.put("request_id", requestId.toString());
        sendNotification(userId, paymentAmount);

        final String gaTag = refillService.getUserGAByRequestId(requestId);
        logger.info("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(paymentAmount, currency.getName(), gaTag);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    @Override
    public boolean logResponse(QuberaRequestDto requestDto) {
        return quberaDao.logResponse(requestDto);
        //todo send email
    }

    @Override
    public AccountQuberaResponseDto createAccount(AccountCreateDto accountCreateDto) {
        User user = userService.findByEmail(accountCreateDto.getEmail());
        Currency currency = currencyService.findByName(accountCreateDto.getCurrencyCode());

        QuberaUserData userData = QuberaUserData.of(accountCreateDto, user.getId(), currency.getId());

        String account = userData.buildAccountString();
        if (account.length() >= thresholdLength) {
            String error = "Count chars of request is over limit {}" + account.length();
            logger.error(error);
            throw new NgDashboardException(error, Constants.ErrorApi.QUBERA_PARAMS_OVER_LIMIT);
        }

        AccountQuberaRequestDto requestDto = new AccountQuberaRequestDto(account, accountCreateDto.getCurrencyCode(), poolId);
        AccountQuberaResponseDto responseDto = kycHttpClient.createAccount(requestDto);
        userData.setIban(responseDto.getIban());
        userData.setAccountNumber(responseDto.getAccountNumber());

        boolean saveUserDetails = quberaDao.saveUserDetails(userData);

        if (saveUserDetails) {
            return responseDto;
        } else {
            throw new NgDashboardException("Error while saving response",
                    Constants.ErrorApi.QUBERA_SAVE_ACCOUNT_RESPONSE_ERROR);
        }
    }

    @Override
    public boolean checkAccountExist(String email, String currencyName) {
        return quberaDao.existAccountByUserEmailAndCurrencyName(email, currencyName);
    }

    @Override
    public AccountInfoDto getInfoAccount(String email) {
        String account = quberaDao.getAccountByUserEmail(email);
        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }
        return kycHttpClient.getBalanceAccount(account);
    }

    @Override
    public ResponsePaymentDto createPaymentToMaster(String email, PaymentRequestDto paymentRequestDto) {
        String account = quberaDao.getAccountByUserEmail(email);

        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }

        QuberaPaymentToMasterDto paymentToMasterDto = new QuberaPaymentToMasterDto();
        paymentToMasterDto.setAmount(paymentRequestDto.getAmount());
        paymentToMasterDto.setAccountNumber(account);
        paymentToMasterDto.setCurrencyCode(paymentRequestDto.getCurrencyCode());
        paymentToMasterDto.setNarrative("Inner transfer");
        return kycHttpClient.createPaymentInternal(paymentToMasterDto, true);
    }

    @Override
    public ResponsePaymentDto createPaymentFromMater(String email, PaymentRequestDto paymentRequestDto) {
        String account = quberaDao.getAccountByUserEmail(email);

        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }

        QuberaPaymentToMasterDto paymentToMasterDto = new QuberaPaymentToMasterDto();
        paymentToMasterDto.setAmount(paymentRequestDto.getAmount());
        paymentToMasterDto.setSenderAccountNumber(account);
        paymentToMasterDto.setBeneficiaryAccountNumber(masterAccount);
        paymentToMasterDto.setCurrencyCode(paymentRequestDto.getCurrencyCode());
        paymentToMasterDto.setNarrative("Inner transfer");
        return kycHttpClient.createPaymentInternal(paymentToMasterDto, false);
    }

    @Override
    public boolean confirmPaymentToMaster(Integer paymentId) {
        return kycHttpClient.confirmInternalPayment(paymentId, true);
    }

    @Override
    public boolean confirmPaymentFRomMaster(Integer paymentId) {
        return kycHttpClient.confirmInternalPayment(paymentId, false);
    }

    @Override
    public ResponsePaymentDto createExternalPayment(ExternalPaymentDto externalPaymentDto, String email) {

        String account = quberaDao.getAccountByUserEmail(email);

        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }

        //check balance of user

        AccountInfoDto balanceAccount = kycHttpClient.getBalanceAccount(account);

        if (balanceAccount.getAvailableBalance().getAmount()
                .compareTo(externalPaymentDto.getTransferDetails().getAmount()) < 0) {
            String messageError = "Not enough money for current payment " +
                    externalPaymentDto.getTransferDetails().getAmount().toPlainString()
                    + "available balance " +
                    balanceAccount.getAvailableBalance().getAmount().toPlainString();
            logger.error(messageError);
            throw new NgDashboardException(messageError, Constants.ErrorApi.QUBERA_NOT_ENOUGH_MONEY_FOR_PAYMENT);
        }

        externalPaymentDto.setSenderAccountNumber(account);
        return kycHttpClient.createExternalPayment(externalPaymentDto);
    }

    @Override
    public String confirmExternalPayment(Integer paymentId) {
        return kycHttpClient.confirmExternalPayment(paymentId);
    }

    @Override
    public QuberaPaymentInfoDto getInfoForPayment(String email) {
        User user = userService.findByEmail(email);

        if (!quberaDao.existAccountByUserEmailAndCurrencyName(email, "EUR")) {
            return null;
        }

        QuberaUserData userData = quberaDao.getUserDataByUserId(user.getId());
        return new QuberaPaymentInfoDto(userData.getIban(), userData.getAccountNumber(), null);
    }

    @Override
    public void sendNotification(QuberaRequestDto quberaRequestDto) {

        Integer userId = quberaDao.findUserIdByAccountNumber(quberaRequestDto.getAccountNumber());
        User user = userService.getUserById(userId);
        String msg;
        UserNotificationMessage message;
        if (quberaRequestDto.getState().equalsIgnoreCase("Rejected")) {
            msg = "Your payment was rejected, reason " + quberaRequestDto.getRejectionReason();
            message = new UserNotificationMessage(WsSourceTypeEnum.FIAT, UserNotificationType.ERROR, msg);
        } else {
            msg = "Your payment was confirmed, amount " + quberaRequestDto.getPaymentAmount().toPlainString() + " " + quberaRequestDto.getCurrency();
            message = new UserNotificationMessage(WsSourceTypeEnum.FIAT, UserNotificationType.SUCCESS, msg);
        }
        try {
            stompMessenger.sendPersonalMessageToUser(user.getEmail(), message);
        } catch (Exception e) {
        }

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("Deposit for your bank account");
        email.setMessage(msg);
        sendMailService.sendInfoMail(email);
    }

    public void sendNotification(int userId, String paymentAmount) {
        User user = userService.getUserById(userId);
        String msg = "Success deposit amount " + paymentAmount + " EUR.";
        UserNotificationMessage message =
                new UserNotificationMessage(WsSourceTypeEnum.FIAT, UserNotificationType.SUCCESS, msg);
        try {
            stompMessenger.sendPersonalMessageToUser(user.getEmail(), message);
        } catch (Exception e) {
        }

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("Deposit fiat");
        email.setMessage(msg);
        sendMailService.sendInfoMail(email);
    }
}
