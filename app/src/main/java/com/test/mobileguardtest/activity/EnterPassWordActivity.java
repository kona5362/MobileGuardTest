package com.test.mobileguardtest.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConstantValue;
import com.test.mobileguardtest.utils.SpUtil;
import com.test.mobileguardtest.utils.ToastUtil;

/**
 * Created by kona on 2017/8/6.
 */

public class EnterPassWordActivity extends Activity {

    private EditText etPwd;
    private String packageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        ImageView ivAppIcon = (ImageView) findViewById(R.id.iv_app_icon);
        Intent intent = getIntent();
        if (intent != null) {
            PackageManager pm = getPackageManager();
            packageName = intent.getStringExtra("packageName");
            try {
                Drawable icon = pm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(pm);
                ivAppIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

/*            //////////
            Intent intentForPackage = pm.getLaunchIntentForPackage(packageName);
            startActivity(intentForPackage);
            /////////*/
        }



    }

    /**
     * 按后退键，退到桌面
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();替代父类的业务逻辑
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }



    public void submit(View view) {
        String inputPassWord = etPwd.getText().toString();
        String passWord = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PWD, null);
        if (TextUtils.isEmpty(inputPassWord)) {
            ToastUtil.show(this,"请输入密码！");
        }else if(inputPassWord.equals(passWord)){
            Intent intent = new Intent();
            intent.setAction("kona.stop.watchdog");
            intent.putExtra("packageName", packageName);
            sendBroadcast(intent);
            finish();


        }else{
            ToastUtil.show(this,"密码错误！");
        }
    }
}
