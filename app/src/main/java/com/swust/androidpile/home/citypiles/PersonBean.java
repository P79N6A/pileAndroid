package com.swust.androidpile.home.citypiles;

public class PersonBean {

	private String Name;//名字
	private String PinYin;//名字的拼音
	private String FirstPinYin;//拼音的第一个大写字母

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPinYin() {
		return PinYin;
	}

	public void setPinYin(String pinYin) {
		PinYin = pinYin;
	}

	public String getFirstPinYin() {
		return FirstPinYin;
	}

	public void setFirstPinYin(String firstPinYin) {
		FirstPinYin = firstPinYin;
	}

	public String toString() {
		return getName();
	}

}
