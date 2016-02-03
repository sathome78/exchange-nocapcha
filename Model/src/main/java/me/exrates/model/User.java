package me.exrates.model;

import java.util.Date;

public class User  {

	private int id;
	private String nickname;
	private String email;
	private int phone;
	private String status;
	private String password;
	private String finpassword;
	private Date regdate;
	private String ip;
	private String confirmPassword;
	private boolean readRules;
	private String role;
	
<<<<<<< HEAD
	public User() {
		
	}

=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isReadRules() {
		return readRules;
	}

<<<<<<< HEAD

=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	public void setReadRules(boolean readRules) {
		this.readRules = readRules;
	}

<<<<<<< HEAD

=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	public String getConfirmPassword() {
		return confirmPassword;
	}

<<<<<<< HEAD

=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

<<<<<<< HEAD

=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

<<<<<<< HEAD
	
=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
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

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public Boolean getStatus() {
<<<<<<< HEAD
		if(status.equals("active")) {
			return true;
		}
		else return false;
=======
		return status.equals("active");
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
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
<<<<<<< HEAD

	
	
	

}
=======
}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
