package com.test.hook.ime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.test.hook.ime.hook.HookIME;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HookIME.hook(this);
    }
}
