package me.exrates.model.form;

import javax.validation.constraints.Size;

/**
 * Created by ogolv on 09.08.2016.
 */
public class FeedbackMessageForm {

    @Size(min=2, max=30)
    private String senderName;
    private String senderEmail;
    @Size(min=2, max=500)
    private String messageText;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        return "FeedbackMessageForm{" +
                "senderName='" + senderName + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", messageText='" + messageText + '\'' +
                '}';
    }
}
