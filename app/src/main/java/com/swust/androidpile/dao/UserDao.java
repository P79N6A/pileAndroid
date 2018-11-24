package com.swust.androidpile.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.swust.androidpile.entity.Charging;
import com.swust.androidpile.entity.User;

public class UserDao {
	private DBOpenHelper helper;
	private SQLiteDatabase db;

	static byte[] bytes = new byte[0];
	private static UserDao userDao = null;

	public UserDao() {
	}

	public  UserDao(Context context) {
		helper = new DBOpenHelper(context);
	}

	public static UserDao getInstance(Context context) {
		synchronized (bytes) {
			if (userDao == null)
				return new UserDao(context);
		}
		return null;
	}
	
	//查询用户
	public User findUser(){
		db=helper.getWritableDatabase();
		Cursor cursor= db.rawQuery("select * from db_user", null);
		if(cursor.moveToNext()){
			User user=new User();
			String name=cursor.getString(cursor.getColumnIndex("name"));
			int id=cursor.getInt(cursor.getColumnIndex("id"));
			String phone=cursor.getString(cursor.getColumnIndex("phone"));
			user.setId(id);
			user.setName(name);
			user.setPhone(phone);
			cursor.close();
			db.close();
			return user;
		}
		cursor.close();
		db.close();
		return null;
	}
	
	//添加用户
	public void addUser(User user){
		db=helper.getWritableDatabase();
		db.execSQL("insert into db_user(name,phone) values (?,?)",
				new Object[]{user.getName(),user.getPhone()});
		updateLoginState(1);
		db.close();
	}
	
	//添加远程充电信息
	public void addCharging(Charging c){
		db=helper.getWritableDatabase();
		db.execSQL("insert into db_charging(phoneDBData) values (?)",
				new Object[]{c.getPhoneDBData()});
		db.close();
	}
	
	//删除远程充电信息
	public void deleteCharging(){
		db=helper.getWritableDatabase();
		db.execSQL(" delete from db_charging");//清空记录
		db.close();
	}
	
	//查询远程充电信息
	public Charging findCharging(){
		db=helper.getWritableDatabase();
		Cursor cursor= db.rawQuery("select * from db_charging", null);
		if(cursor.moveToNext()){
			Charging c = new Charging();
			String phoneDBData=cursor.getString(cursor.getColumnIndex("phoneDBData"));
			c.setPhoneDBData(phoneDBData);
			cursor.close();
			db.close();
			return c;
		}else{
			cursor.close();
			db.close();
			return null;
		}
	}
		
	//查询登录状态
	public int findLoginState(){
		db=helper.getWritableDatabase();
		Cursor cursor= db.rawQuery("select * from db_constant", null);
		if(cursor.moveToNext()){
			int state=cursor.getInt(cursor.getColumnIndex("login"));
			cursor.close();
			db.close();
			return state;
		}else{
			db.execSQL("insert into db_constant(login) values (?)",
					new Object[]{0});
			cursor.close();
			db.close();
			return 0;
		}
	}
	
	//更新登录状态
	public void updateLoginState(int login){
		db=helper.getWritableDatabase();
		Cursor cursor= db.rawQuery("select * from db_constant", null);
		if(cursor.moveToNext()){
			db.execSQL("update db_constant set login=?",new Object[]{login});
		}
		cursor.close();
		db.close();
	}
	
	//更新用户信息
	public void updateUser(User user){
		db=helper.getWritableDatabase();
		Cursor cursor= db.rawQuery("select * from db_user", null);
		if(cursor.moveToNext()){
			db.execSQL("update db_user set phone=?",new Object[]{user.getPhone()});
		}
		cursor.close();
		db.close();
	}

	public void deleteUser(User user) {
		db=helper.getWritableDatabase();
		db.execSQL(" delete from db_user");//清空记录
		db.close();
	}
	
}
