package com.swust.androidpile.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swust.androidpile.entity.Charging;
import com.swust.androidpile.entity.Notes;

public class ChargingDao {
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    static byte[] bytes = new byte[0];
    private static ChargingDao chargingDao = null;

    private ChargingDao() {
    }

    private ChargingDao(Context context) {
        helper = new DBOpenHelper(context);
    }

    public static ChargingDao getInstance(Context context) {
        synchronized (bytes) {
            if (chargingDao == null)
                return new ChargingDao(context);
        }
        return null;
    }

    //添加远程充电信息
    public void addCharging(Charging c) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into db_charging(phoneDBData) values (?)",
                new Object[]{c.getPhoneDBData()});
        db.close();
    }

    //添加充电参数
    public void addNotes(Notes n) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into db_notes(notes) values (?)",
                new Object[]{n.getNotes()});
        db.close();
    }

    //删除远程充电信息
    public void deleteCharging() {
        db = helper.getWritableDatabase();
        db.execSQL(" delete from db_charging");//清空记录
        db.close();
    }

    //删除充电参数
    public void deleteNotes() {
        db = helper.getWritableDatabase();
        db.execSQL(" delete from db_notes");//清空记录
        db.close();
    }

    //查询远程充电信息
    public Charging findCharging() {
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from db_charging", null);
        if (cursor.moveToNext()) {
            Charging c = new Charging();
            String phoneDBData = cursor.getString(cursor.getColumnIndex("phoneDBData"));
            c.setPhoneDBData(phoneDBData);
            cursor.close();
            db.close();
            return c;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    //查询充电参数
    public Notes findNotes() {
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from db_notes", null);
        if (cursor.moveToNext()) {
            Notes c = new Notes();
            String notes = cursor.getString(cursor.getColumnIndex("notes"));
            c.setNotes(notes);
            cursor.close();
            db.close();
            return c;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

}
