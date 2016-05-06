package me.exrates.model;

import me.exrates.model.enums.TokenType;

import java.time.LocalDateTime;

public class TemporalToken {
	
	private int id;
	private String value;
	private int userId;
	private boolean expired;
	private LocalDateTime dateCreation;
	private TokenType tokenType;
	private String checkIp;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public boolean isExpired() {
		return expired;
	}
	public void setExpired(boolean expired) {
		this.expired = expired;
	}
	public LocalDateTime getDateCreation() {
		return dateCreation;
	}
	public void setDateCreation(LocalDateTime dateCreation) {
		this.dateCreation = dateCreation;
	}
	public TokenType getTokenType() {
		return tokenType;
	}
	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public String getCheckIp() {
		return checkIp;
	}

	public void setCheckIp(String checkIp) {
		this.checkIp = checkIp;
	}
}
