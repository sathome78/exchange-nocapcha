package me.exrates.service.util;

import me.exrates.service.notifications.sms.epochta.Phones;
import me.exrates.service.notifications.sms.epochta.RequestBuilder;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Maks on 17.10.2017.
 */
public class EApi {

    private RequestBuilder reqBuilder;
    private String login;
    private String password;

    public EApi(RequestBuilder reqBuilder,String login, String password) {
        this.reqBuilder = reqBuilder;
        this.login=login;
        this.password=password;
    }


    public String getStatus(String msgId){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS><operations><operation>GETSTATUS</operation></operations>");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification>");
        request=request.concat("<statistics>");
        request=request.concat("<messageid>"+msgId+"</messageid>");
        request=request.concat("</statistics>");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }

    public String getPrice(String text, Map<String, String>  phones){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS>");
        request=request.concat("<operations>  ");
        request=request.concat("<operation>GETPRICE</operation>");
        request=request.concat("</operations> ");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification>");
        request=request.concat("<message>");
        request=request.concat("<sender>SMS</sender>");
        request=request.concat("<text>"+text+"</text>");
        request=request.concat("</message>");
        request=request.concat("<numbers>");
        for (Map.Entry entry : phones.entrySet()) {
            request=request.concat("<number messageID=\""+entry.getKey()+"\">"+entry.getValue()+"</number>");
        }
        request=request.concat("</numbers>");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }

    public String getBalance(){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS>");
        request=request.concat("<operations>");
        request=request.concat("<operation>BALANCE</operation>");
        request=request.concat("</operations>");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification> ");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }

    public String sendSms(String sender, String text, ArrayList<Phones> phones){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS>");
        request=request.concat("<operations>");
        request=request.concat("<operation>SEND</operation>");
        request=request.concat("</operations>");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification>");
        request=request.concat("<message>");
        request=request.concat("<sender>"+sender+"</sender>");
        request=request.concat("<text>"+text+"</text>");
        request=request.concat("</message>");
        request=request.concat("<numbers>");
        for (Phones phone : phones) {
            request=request.concat("<number");
            if(phone.getIdMessage().length()>0) request=request.concat(" messageID=\""+phone.getIdMessage()+"\"");
            if(phone.getVaraibles().length()>0) request=request.concat(" variables=\""+phone.getVaraibles()+"\"");
            request=request.concat(">");
            request=request.concat(phone.getPhone());
            request=request.concat("</number>");
        }

        request=request.concat("</numbers>");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }
}
