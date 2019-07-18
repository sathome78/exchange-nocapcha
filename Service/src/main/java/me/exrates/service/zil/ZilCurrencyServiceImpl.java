package me.exrates.service.zil;

import com.firestack.laksaj.account.Wallet;
import com.firestack.laksaj.crypto.KeyTools;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.transaction.Transaction;
import com.firestack.laksaj.transaction.TransactionFactory;
import com.firestack.laksaj.utils.Bech32;
import me.exrates.model.condition.MonolitConditional;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;

import static com.firestack.laksaj.account.Wallet.pack;

@Service
@Conditional(MonolitConditional.class)
public class ZilCurrencyServiceImpl implements ZilCurrencyService{

    public static void main(String[] args) {
        ZilCurrencyServiceImpl zilCurrencyService = new ZilCurrencyServiceImpl();
//        System.out.println(zilCurrencyService.generatePrivateKey());
        String pub = zilCurrencyService.getPublicKeyFromPrivateKey("b80c60093b3cacdf174cb8e2f2f403e8f178a241da3c2137859da8a206479916");
        System.out.println(pub);
    }
    public String generatePrivateKey(){
        String privKey = "";
        try {
            privKey = KeyTools.generatePrivateKey();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return privKey;
    }

    public String getPublicKeyFromPrivateKey(String privKey){
        return KeyTools.getPublicKeyFromPrivateKey(privKey, true);
    }

    public String getAddressFromPrivateKey(String privKey){
        String address = KeyTools.getAddressFromPrivateKey(privKey);
        try {
            return Bech32.toBech32Address(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String createTransaction(Map<String, String> params) throws Exception {
        Wallet wallet = new Wallet();
        wallet.addByPrivateKey("b80c60093b3cacdf174cb8e2f2f403e8f178a241da3c2137859da8a206479916");
        Transaction transaction = Transaction.builder()
                .version(String.valueOf(pack(1, 1)))
                .toAddr("zil1rk0jzyetaur8uwhqdgdenrp8h4fakq87tl0f8l".toLowerCase())
                .senderPubKey("02783C6C7946DE9D1350297CF40BED9C14282BBAC4B9496B896227B9A3AC8635CC".toLowerCase())
                .amount("100000000000")
                .gasPrice("1000000000")
                .gasLimit("1")
                .code("")
                .data("")
                .provider(new HttpProvider("https://api.zilliqa.com"))
                .build();
        transaction = wallet.sign(transaction);

        // Send a transaction to the network
        HttpProvider.CreateTxResult result = TransactionFactory.createTransaction(transaction);
        System.out.println(result);
        return "";
    }
}
