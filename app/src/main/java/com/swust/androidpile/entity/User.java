package com.swust.androidpile.entity;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private int id;
	private String phone;
	
	public User(){}
	public User(String name,String phone){
		this.name=name;
		this.phone=phone;
	}
	public User(int id,String name){
		this.name=name;
		this.id=id;
	}
	public User(String name){
		this.name=name;;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
