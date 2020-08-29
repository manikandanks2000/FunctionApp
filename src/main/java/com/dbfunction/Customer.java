package com.dbfunction;



import java.io.Serializable;

public class Customer implements Serializable{

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getuserid() {
		return userid;
	}
	public void setuserid(int userid) {
		this.userid = userid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	private int id=0;
	private int userid=0;
	
	private String email=null;
	
	private String fname=null;
	
	private String lname=null;
	private String fullname=null;
	
}
