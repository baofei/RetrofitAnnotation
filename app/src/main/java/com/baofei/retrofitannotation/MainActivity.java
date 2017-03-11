package com.baofei.retrofitannotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baofei.library.callback.BaseResponse;
import com.baofei.library.retrofit.RetrofitManager;
import com.baofei.retrofitannotation.net.manager.ApiServiceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    TextView response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetrofitManager.getInstance().init(this, "http://wwww.ddd.com");
        setContentView(R.layout.activity_main);
        response = (TextView) findViewById(R.id.response);
        findViewById(R.id.getBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiServiceManager.getInstance().getDefaultAddr();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseResponse event) {
        response.setText(event.toString());

    }
}
