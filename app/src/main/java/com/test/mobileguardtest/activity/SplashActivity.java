package com.test.mobileguardtest.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.mobileguardtest.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;
import com.test.mobileguardtest.utils.StreamUtil;
import com.test.mobileguardtest.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	protected static final int UPDATE_VERSION = 100;
	protected static final int ENTER_HOME = 101;
	protected static final int URL_ERROR = 102;
	protected static final int IO_ERROR = 103;
	protected static final int JSON_ERROR = 104;
	private TextView tv_version_name;
	private int mLocalVersionCode;
	protected String mVersionDes;
	private String mdownloadUrl;
	private String tag = "SplashActivity";
	
	private Handler mhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				showUpdateDialog();
				break;
			case ENTER_HOME:
				enterHome();
				break;
			case URL_ERROR:
				ToastUtil.show(getApplicationContext(),"Url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(getApplicationContext(),"IO异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(getApplicationContext(),"JSON异常");
				enterHome();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	private RelativeLayout rl_root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//初始化UI
		initUI();
		//初始化数据
		initData();
		
		//初始化动画
		initAnimation();
		
		//初始化数据库
		initDB();
	}

	private void initDB() {
		initAddressDB("address.db");
	}

	private void initAddressDB(String dbName) {//TODO
		File dir = getFilesDir();
		File file = new File(dir,dbName);
		if(file.exists()){
			return;
		}
		AssetManager assets = getAssets();
		InputStream is = null;
		OutputStream os = null;
		try {
			is = assets.open(dbName);
			os = new FileOutputStream(file);
			int length = -1;
			byte[] buf = new byte[1024];
			while((length = is.read(buf))!=-1){
				os.write(buf, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.setAnimation(alphaAnimation);
	}

	protected void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		finish();
	}

	protected void showUpdateDialog() {
		Builder builder = new AlertDialog.Builder(this);//TODO
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("版本更新");
		builder.setMessage(mVersionDes);
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadAPK();//根据拿到的地址下载新版的APK
			}
		});
		builder.setNegativeButton("稍后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		builder.show();
		
			
	}
	/**
	 * 下载新版的APK
	 */
	protected void downloadAPK() {
		//TODO 忘了下一步
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MobileGuardTest.apk";//TODO
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(mdownloadUrl, path,new RequestCallBack<File>() {
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					Log.i(tag,"下载成功");
					File file = responseInfo.result;
					installAPK(file);
				}
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(tag,"下载失败");
				}
				@Override
				public void onStart() {
					Log.i(tag,"开始下载");
					super.onStart();
				}
				
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(tag,"=====下载中=====");
					Log.i(tag,"total:"+total);
					Log.i(tag,"current:"+current);
					Log.i(tag,"=====下载中=====");
					super.onLoading(total, current, isUploading);
				}
			});
		}
	}
	/**
	 * 安装APK
	 * @param file APK路径
	 */
	protected void installAPK(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//TODO Uri.fromFile(file)
		startActivityForResult(intent, 1);//防止用户取消安装退回到这个界面
	}
	
	//一旦此方法被调用说明，用户退回到这个界面了，直接进入主界面
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
/*		PackageManager pm = getPackageManager();//TODO  居然不会创建包管理者！！
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;
			mLocalVersionCode = packageInfo.versionCode;
			tv_version_name.setText(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}*/   //学会细化功能
		
		tv_version_name.setText(getVersionName());//设置应用版本名
		mLocalVersionCode = getVersionCode(); //得到应用版本号
		//检查是否开启自动更新
		boolean isOpen = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		if(isOpen){
			checkVersion();//检查版本是否有更新
		}else{
			mhandler.sendEmptyMessageDelayed(ENTER_HOME, 1000);
		}
		
	}

	/**
	 * 得到本地应用版本号 
	 * @return 本地应用版本号
	 */
	private int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);//TODO 这一步都不会！！！
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 检查版本是否有更新
	 */
	private void checkVersion() {//TODO 完全忘了该方法怎么写
		new Thread(){
			public void run() {
				long startTime = System.currentTimeMillis();//TODO
				Message msg = Message.obtain();
				try {					
					URL url = new URL("http://192.168.1.110:8080/update.json");//TODO
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();//TODO
					connection.setConnectTimeout(2000);//TODO
					connection.setReadTimeout(2000);
					if(connection.getResponseCode()==200){ //TODO 1.忘记写    2.因为权限问题  浪费半个小时！   其实异常里有写，不仔细看！
						InputStream is = connection.getInputStream();//TODO
						String json = StreamUtil.stream2String(is);
						JSONObject jsonObject = new JSONObject(json);
						mVersionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject.getString("versionCode");
						mdownloadUrl = jsonObject.getString("downloadUrl");
						Log.i(tag, "mVersionDes="+mVersionDes);
						Log.i(tag, "versionCode="+versionCode);
						Log.i(tag, "mdownloadUrl="+mdownloadUrl);
						
						if(mLocalVersionCode<Integer.parseInt(versionCode)){
							//下载APK
							msg.what = UPDATE_VERSION;
						}else{
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = IO_ERROR;
					e.printStackTrace();
				}catch (JSONException e){
					msg.what = JSON_ERROR;
					e.printStackTrace();
				}finally{
					long endTime = System.currentTimeMillis();
					if(endTime-startTime<4000){
						try {
							Thread.sleep(4000-(endTime-startTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mhandler.sendMessage(msg);
				}
				
			};
		}.start();
	}

	/**
	 * 得到版本号
	 * @return
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);//TODO 这一步都不会！！！
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
	

}
