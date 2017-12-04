package com.test.hook.ime.hook;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by yanchen on 17-11-30.
 */

public class HookIME {
    public static void hook(final Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            Field field = inputMethodManager.getClass().getDeclaredField("mService");
            field.setAccessible(true);
            Object object = field.get(inputMethodManager);

            InputMethodManagerServiceHandler invocationHandler = new InputMethodManagerServiceHandler(inputMethodManager,object);
            Object proxyInstance = Proxy.newProxyInstance(object.getClass().getClassLoader(),
                    object.getClass().getInterfaces(),
                    invocationHandler);
            field.set(inputMethodManager,proxyInstance);

        }catch (Throwable e){
            e.printStackTrace();
        }
    }
}
