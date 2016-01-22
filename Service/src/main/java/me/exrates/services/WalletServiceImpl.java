//package me.exrates.services;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import me.exrates.beans.Currency;
//import me.exrates.beans.Wallet;
//import me.exrates.daos.CurrencyDao;
//import me.exrates.daos.UserDao;
//import me.exrates.daos.WalletDao;
//
//@Service("walletService")
//public class WalletServiceImpl implements WalletService{
//
//	@Autowired
//	WalletDao walletDao;
//
//	@Autowired
//	CurrencyDao currencyDao;
//
//	@Override
//	public List<Wallet> getAllWallets(int userId) {
//		List<Wallet> walletList = walletDao.getAllWallets(userId);
//		List<Currency> currList = currencyDao.getCurrList();
//		for(Wallet wallet : walletList) {
//			for(Currency currency : currList) {
//				if(wallet.getCurrId() == currency.getId()) {
//					wallet.setName(currency.getName());
//				}
//			}
//		}
//		return walletList;
//	}
//
//	@Override
//	public List<Currency> getCurrencyList() {
//		return currencyDao.getCurrList();
//	}
//
//
//
//
//
//
//
//}
