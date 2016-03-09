package me.exrates.service;

import me.exrates.model.Email;

public interface SendMailService {

	public boolean sendMail(Email email);
}
