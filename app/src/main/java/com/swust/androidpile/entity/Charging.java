package com.swust.androidpile.entity;

public class Charging {
	String phoneDBData;	//app发送给网关的充电指令
	
	public Charging(){}
	public Charging(String phoneDBData){
		this.phoneDBData=phoneDBData;
	}
	
	public String getPhoneDBData() {
		return phoneDBData;
	}
	public void setPhoneDBData(String phoneDBData) {
		this.phoneDBData = phoneDBData;
	}
	
	
	
}
