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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SendMailServiceImpl implements SendMailService{

	@Autowired
	private JavaMailSender mailSender;
	
	private static final Logger logger = LogManager.getLogger(SendMailServiceImpl.class);

	public void sendMail(Email email){
		email.setFrom("exrates.me@mail.ru");
        mailSender.send(new MimeMessagePreparator() {
			  public void prepare(MimeMessage mimeMessage) throws MessagingException {
			    MimeMessageHelper message;
				message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				message.setFrom(email.getFrom());
			    message.setTo(email.getTo());
			    message.setSubject(email.getSubject());
			    message.setText(email.getMessage(), true);
			  }
			});
}
	
}
