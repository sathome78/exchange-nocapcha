package me.exrates.service.qiwi;

import lombok.extern.log4j.Log4j;
import me.exrates.model.dto.qiwi.request.QiwiRequest;
import me.exrates.model.dto.qiwi.request.QiwiRequestGetTransactions;
import me.exrates.model.dto.qiwi.request.QiwiRequestHeader;
import me.exrates.model.dto.qiwi.response.QiwiResponse;
import me.exrates.model.dto.qiwi.response.QiwiResponseP2PInvoice;
import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j
public class QiwiExternalServiceImpl implements QiwiExternalService{

    @Autowired
    @Qualifier("qiwiRestTemplate")
    private RestTemplate qiwiRestTemplate;

    public String generateUniqMemo(int userId) {
        QiwiRequestHeader requestHeader = new QiwiRequestHeader("p2pInvoiceRequest");

        QiwiRequest request = new QiwiRequest(requestHeader, null);

        ResponseEntity<QiwiResponseP2PInvoice> response = qiwiRestTemplate.postForEntity("https://api.adgroup.finance/transfer/tx-merchant-wallet", request , QiwiResponseP2PInvoice.class );

        log.info("*** Qiwi *** | Generate new uniq memo. UserId:"+userId+" | Memo:"+response.getBody().getResponseData().getComment());

        return response.getBody().getResponseData().getComment();
    }

    public QiwiResponseTransaction[] getLastTransactions() {
        QiwiRequestHeader requestHeader = new QiwiRequestHeader("txName");
        String[] status = {"CREATED", "PENDING", "APPROVED", "REJECTED"};
        String[] type = {"CREATED", "PENDING", "APPROVED", "REJECTED"};
        QiwiRequestGetTransactions requestBody = new QiwiRequestGetTransactions();

        QiwiRequest request = new QiwiRequest(requestHeader, requestBody);

        ResponseEntity<QiwiResponse> response = qiwiRestTemplate.postForEntity("https://api.adgroup.finance/transfer/get-merchant-tx", request , QiwiResponse.class );

        QiwiResponseTransaction[] trans = response.getBody().getResponseData().getTransactions();

        for (QiwiResponseTransaction element: trans) {
            System.out.println(element.get_id());
        }

        return response.getBody().getResponseData().getTransactions();
    }
}
