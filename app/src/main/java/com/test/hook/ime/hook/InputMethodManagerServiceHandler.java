package com.test.hook.ime.hook;

import android.os.Build;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by yanchen on 17-11-30.
 */

public class InputMethodManagerServiceHandler implements InvocationHandler {
    private Object mInputMethodManagerService;
    private Object mInputMethodManager;
    private Object proxyObject = null;

    InputMethodManagerServiceHandler(Object inputMethodManager, Object inputMethodManagerService) {
        super();
        mInputMethodManager = inputMethodManager;
        mInputMethodManagerService = inputMethodManagerService;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (mInputMethodManager == null) {
            return method.invoke(mInputMethodManagerService, objects);
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (method.getName().equals("startInputOrWindowGainedFocus")) {
                    Field field = mInputMethodManager.getClass().getDeclaredField("mServedInputConnectionWrapper");
                    field.setAccessible(true);
                    Object mServedInputConnectionWrapper = field.get(mInputMethodManager);


                    field = mServedInputConnectionWrapper.getClass().getSuperclass().getDeclaredField("mInputConnection");
                    field.setAccessible(true);
                    Object mInputConnection = field.get(mServedInputConnectionWrapper);


                    InputConnectionHandler invocationHandler = new InputConnectionHandler(mInputMethodManager, mInputConnection);
                    Object instance = Proxy.newProxyInstance(mInputConnection.getClass().getClassLoader(),
                            Class.forName("android.view.inputmethod.BaseInputConnection").getInterfaces(),
                            invocationHandler);

                    field.set(mServedInputConnectionWrapper, instance);

                }
            } else {
                if (method.getName().equals("windowGainedFocus") || method.getName().equals("startInput")) {


                    Field field = mInputMethodManager.getClass().getDeclaredField("mServedInputConnectionWrapper");
                    field.setAccessible(true);
                    Object mServedInputConnectionWrapper = field.get(mInputMethodManager);


                    field = mServedInputConnectionWrapper.getClass().getSuperclass().getDeclaredField("mInputConnection");
                    field.setAccessible(true);
                    WeakReference<InputConnection> mInputConnection = (WeakReference<InputConnection>) field.get(mServedInputConnectionWrapper);

                    InputConnection inputConnectionOb = mInputConnection.get();


                    InputConnectionHandler invocationHandler = new InputConnectionHandler(mInputMethodManager, inputConnectionOb);
                    Object instance = Proxy.newProxyInstance(inputConnectionOb.getClass().getClassLoader(),
                            Class.forName("android.view.inputmethod.BaseInputConnection").getInterfaces(),
                            invocationHandler);

                    proxyObject = instance;

                    WeakReference<Object> wr = new WeakReference<Object>(proxyObject);

                    field.set(mServedInputConnectionWrapper, wr);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return method.invoke(mInputMethodManagerService, objects);

    }
}
