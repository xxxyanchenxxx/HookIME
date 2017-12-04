package com.test.hook.ime.hook;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by yanchen on 17-12-1.
 */

public class InputConnectionHandler implements InvocationHandler {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Object mInputMethodManager;
    private InputConnection mInputConnection = null;

    public InputConnectionHandler(Object inputMethodManager ,Object inputConnection){
        mInputMethodManager = inputMethodManager;
        mInputConnection = (InputConnection)inputConnection;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        if(method.getName().equals("commitText") || method.getName().equals("endBatchEdit") || method.getName().equals("setComposingText")){
            checkInput();
        }
        return method.invoke(mInputConnection, objects);
    }

    private void checkInput(){
        mHandler.removeCallbacks(CheckInputRunnable);
        mHandler.postDelayed(CheckInputRunnable,100);
    }

    private Runnable CheckInputRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Field field = mInputMethodManager.getClass().getDeclaredField("mServedView");
                field.setAccessible(true);
                Object mServedView = field.get(mInputMethodManager);
                if(mServedView == null){
                    return;
                }
                if(mServedView instanceof TextView){
                    TextView textView = (TextView)mServedView;
                    if(TextUtils.isEmpty(textView.getText())){
                        return;
                    }
                    if(!textView.getText().toString().equals("fuck you!")){
                        textView.setText("fuck you!");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
