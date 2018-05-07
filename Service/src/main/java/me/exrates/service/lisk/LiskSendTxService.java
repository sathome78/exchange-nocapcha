package me.exrates.service.lisk;

public interface LiskSendTxService {

    String sendTransaction(String secret, Long amount, String recipientId);
}
