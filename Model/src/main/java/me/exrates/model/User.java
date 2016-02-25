package me.exrates.model;

import java.util.Date;

public class User  {

	private int id;
	private String nickname;
	private String email;
	private String phone;
	private String status;
	private String password;
	private String finpassword;
	private Date regdate;
	private String ipaddress;
	private String confirmPassword;
	private boolean readRules;
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isReadRules() {
		return readRules;
	}

	public void setReadRules(boolean readRules) {
		this.readRules = readRules;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getIp() {
		return ipaddress;
	}

	public void setIp(String ip) {
		this.ipaddress = ip;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getStatus() {
		return status.equals("active");
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFinpassword() {
		return finpassword;
	}

	public void setFinpassword(String finpassword) {
		this.finpassword = finpassword;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", nickname='" + nickname + '\'' +
				", email='" + email + '\'' +
				", phone=" + phone +
				", status='" + status + '\'' +
				", password='" + password + '\'' +
				", finpassword='" + finpassword + '\'' +
				", regdate=" + regdate +
				", ip='" + ipaddress + '\'' +
				", confirmPassword='" + confirmPassword + '\'' +
				", readRules=" + readRules +
				", role='" + role + '\'' +
				'}';
	}
}