package com.swust.androidpile.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

	public DBOpenHelper(Context context) {
		super(context, "pile.db", null, 1);
//		super(context, "pile.db", null, 2);//当更新app的时候，会查看这行代码，2大于1，会执行最下面的onUpgrade方法
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//用户表
		db.execSQL("create table db_user(id integer primary key autoincrement,"
				+ "	name varchar(20), phone varchar(11)   )");
		
		//登录常量，判断用户是否已经登录登录
		db.execSQL("create table db_constant(login integer)");
		
		//需要的数据：即app发送给网关的充电指令的-------------数据包
		db.execSQL("create table db_charging(id integer primary key autoincrement,phoneDBData varchar(72)) ");

		//充电参数：充电方式、充电预设值、辅助电源电压、电桩编号、充电端口
		db.execSQL("create table db_notes(id integer primary key autoincrement,notes varchar(100))");

//		//预约表
//		db.execSQL("create table db_order(sid integer ,pid integer ,num integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		 Log.i("test", "oldVersion="+oldVersion+"  ,newVersion="+newVersion);
//		 for (int j = oldVersion; j <= newVersion; j++) {	//for循环是为了循序渐渐的更新手机数据库不至于出错
//			 switch (j) {
//			    case 2:
//			        db.execSQL("alter table db_user add column phone varchar(11)");	//给用户表添加字段：电话号码
////			        db.execSQL("create table db_charging(gateId varchar(10),pileId varchar(10),"
////							+ " pilePort varchar(2),cardId varchar(20),phone varchar(12),"
////							+ " pileFlag Integer) ");
//			        break;
//			    default:
//			        break;
//			 } 
//		 }
	}

}
