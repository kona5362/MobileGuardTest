package com.test.mobileguardtest.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.mobileguardtest.R;
import com.test.mobileguardtest.utils.ConvertSizeUtil;

/**
 * Created by kona on 2017/6/20.
 */

public class ProgressDesView extends LinearLayout {

    private TextView tv_title_des;
    private TextView tv_left;
    private TextView tv_right;
    private ProgressBar pb_des;

    public ProgressDesView(Context context) {
        this(context, null);
    }

    public ProgressDesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.progress_des_view, this);
        tv_title_des = (TextView) view.findViewById(R.id.tv_title_des);
        tv_left = (TextView) view.findViewById(R.id.tv_left);
        tv_right = (TextView) view.findViewById(R.id.tv_right);
        pb_des = (ProgressBar) view.findViewById(R.id.pb_des);

        String title_des = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title_des");
        if(!title_des.isEmpty()){
            setTitle(title_des);
        }

    }

    public ProgressDesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String titleName){
        tv_title_des.setText(titleName);
    }

    public void setProgress(int progress){
        pb_des.setProgress(progress);
    }

   /* public void setMemoryAvailable(long value) {
        String result = ConvertSizeUtil.convertSize(value);
        tv_right.setText(result);
    }

    public void setMemoryUsed(long value) {
        String result = ConvertSizeUtil.convertSize(value);
        tv_left.setText(result);
    }*/
   public void setProgressLeftWord(String value) {
       tv_left.setText(value);
   }

    public void setProgressRightWord(String value) {
        tv_right.setText(value);
    }

    public void setTotalProgress(int totalProgress){
        pb_des.setMax(totalProgress);
    }

}
