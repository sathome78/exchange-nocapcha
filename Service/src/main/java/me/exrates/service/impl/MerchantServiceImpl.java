package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.MerchantDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import me.exrates.service.exception.UnsupportedMerchantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class MerchantServiceImpl implements MerchantService{

    @Autowired
    private MerchantDao merchantDao;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    UserService userService;

    @Autowired
    CompanyWalletService companyWalletService;

    @Autowired
    WalletService walletService;

    @Autowired
    CurrencyService currencyService;

    @Override
    public Merchant create(Merchant merchant) {
        return merchantDao.create(merchant);
    }

    @Override
    public List<Merchant> findAllByCurrency(Currency currency) {
        return merchantDao.findAllByCurrency(currency.getId());
    }

    @Override
    public Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies) {
        return currencies.stream()
                .map(Currency::getId)
                .map(currencyId -> new Pair<>(currencyId, merchantDao.findAllByCurrency(currencyId)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public Merchant findById(int id) {
        return merchantDao.findById(id);
    }

    public Optional<CreditsOperation> prepareCreditsOperation(Payment payment,String userEmail) {
        final OperationType operationType = payment.getOperationType();
        final BigDecimal amount = BigDecimal.valueOf(payment.getSum());
        final Merchant merchant = merchantDao.findById(payment.getMerchant());
        final Currency currency = currencyService.findById(payment.getCurrency());
        final String destination = payment.getDestination();
        try {
            if (!isPayable(merchant,currency,amount)) {
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            final String exceptionMessage = "MerchantService".concat(operationType == OperationType.INPUT ?
                    "Input" : "Output");
            throw new UnsupportedMerchantException(exceptionMessage);
        }
        final Commission commissionByType = commissionService.findCommissionByType(operationType);
        final BigDecimal commissionAmount = 
                 commissionByType.getValue()
                .setScale(9,BigDecimal.ROUND_CEILING)
                .multiply(amount)
                .divide(BigDecimal.valueOf(100),BigDecimal.ROUND_CEILING);
        final User user = userService.findByEmail(userEmail);

        final CreditsOperation creditsOperation = new CreditsOperation.Builder()
                .amount(amount)
                .commissionAmount(commissionAmount)
                .commission(commissionByType)
                .operationType(operationType)
                .user(user)
                .currency(currency)
                .merchant(merchant)
                .destination(destination)
                .build();
        return Optional.of(creditsOperation);
    }

    protected boolean isPayable(Merchant merchant, Currency currency, BigDecimal sum) {
        final BigDecimal minSum = merchantDao.getMinSum(merchant.getId(), currency.getId());
        return sum.compareTo(minSum) >= 0;
    }
}