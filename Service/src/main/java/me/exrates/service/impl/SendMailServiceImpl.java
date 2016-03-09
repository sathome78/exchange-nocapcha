package me.exrates.service.impl;

import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import me.exrates.model.Email;
import me.exrates.service.SendMailService;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class SendMailServiceImpl implements SendMailService{

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	MessageSource messageSource;
	
	private static final Locale ru = new Locale("ru");
	
	private static final Logger logger = LogManager.getLogger(SendMailServiceImpl.class);

	public boolean sendMail(Email email) {
		Boolean flag = false;
		email.setMessage(messageSource.getMessage("emailsubmitregister.text", null, ru));
		email.setSubject(messageSource.getMessage("emailsubmitregister.subject", null, ru));
		email.setFrom("exrates@exrates.me");
		try {
		mailSender.send(new MimeMessagePreparator() {
			  public void prepare(MimeMessage mimeMessage) {
			    MimeMessageHelper message;
				try {
					message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
					message.setFrom(email.getFrom());
				    message.setTo(email.getTo());
				    message.setSubject(email.getSubject());
				    message.setText(email.getMessage(), true);
				} catch (MessagingException e) {
					e.printStackTrace();
					logger.error(e.getLocalizedMessage());
				}
			    
			  }
			});
			flag=true;
		} catch(MailException e) {
			e.printStackTrace();
			logger.error("Can't send mail: "+e.getLocalizedMessage());
		}
		return flag;
}
	
}
