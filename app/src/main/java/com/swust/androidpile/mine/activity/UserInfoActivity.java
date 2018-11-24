package com.swust.androidpile.mine.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserInfoActivity extends Activity implements OnClickListener{
	
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private ImageView photo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_mine_user_info);
		init();
	}
	
	public void init(){
		initUserHeader();
		returnUserInfo();
		initNameAndPhone();
		setListener();
	}

	private void setListener() {
		findViewById(R.id.image1).setOnClickListener(this);//返回监听
		findViewById(R.id.relative2).setOnClickListener(this);//头像监听
	}

	/**
	 * 初始化个人头像
	 */
	private void initUserHeader() {
		ImageView image = (ImageView)findViewById(R.id.image);
		try {
			File file = new File(Environment.getExternalStorageDirectory().getPath()+
        			"/userHeader");
			FileInputStream fis = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			image.setImageBitmap(bitmap);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新调用页的头像
	 */
	private void returnUserInfo(){
		Intent intent = getIntent();
		setResult(0x102,intent);
	}

	/**
	 * 初始化个人姓名与号码
	 */
	private void initNameAndPhone() {
		UserDao dao = new UserDao(this);
		User user = dao.findUser();
		String name = user.getName();
		String phone=user.getPhone();
//			Log.i("test", name+","+phone);
		TextView text2 = (TextView) findViewById(R.id.text2);
		TextView text3 = (TextView) findViewById(R.id.text3);
		text2.setText(name);
		phone = phone.substring(0, 3)+"****"+phone.substring(7);
		text3.setText(phone);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.relative2://“头像”监听
			// 激活系统图库，选择一张图片
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
			break;
		case R.id.image1:
			finish();
			break;
		}
	}

	/*
    * 剪切图片
    */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
//        	Log.i("test", "PHOTO_REQUEST_GALLERY");
            // 从相册返回的数据
            if (data != null) {
//				Log.i("test", "PHOTO_REQUEST_GALLERY");
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        } 
        else if (requestCode == PHOTO_REQUEST_CUT) {
//        	Log.i("test", "PHOTO_REQUEST_CUT");
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                if(bitmap!=null){	//防止没有保存就直接返回
                	//人物头像显示区域显示圆形图片
                	photo= (ImageView) findViewById(R.id.image);
                	photo.setImageBitmap(bitmap);
                	//将bitmap转换成图片保存为文件到本地中
                	File f = new File(
                			Environment.getExternalStorageDirectory().getPath()+
                			"/userHeader");
	            	try {
                        FileOutputStream bos = new FileOutputStream(f);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
                }
            }
        }
    }
}
