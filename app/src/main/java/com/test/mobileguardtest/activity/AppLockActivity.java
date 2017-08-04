package com.test.mobileguardtest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.test.mobileguardtest.R;

/**
 * Created by kona on 2017/8/4.
 */

public class AppLockActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool_app_lock);

    }
}
