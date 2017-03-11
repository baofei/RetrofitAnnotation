package com.baofei.library.callback;

import org.greenrobot.eventbus.EventBus;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Baofei on 2016-09-20.
 */
public class ApCallBack<T extends BaseResponse> implements Callback<T> {

    T body;

    private Object extra;

    public ApCallBack(Class<? extends BaseResponse> body) {
        this(body, null);
    }

    public ApCallBack(Class<? extends BaseResponse> body, Object extra) {
        this.extra = extra;
        try {
            this.body = (T) body.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            BaseResponse bean = response.body();
            if (extra != null) {
                bean.extra = extra;
            }
            EventBus.getDefault().post(bean);
        } else {
            onFailure(call, new Throwable("HTTP STATUS CODE " + response.code()));
        }

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (body == null) {
            body = (T) new BaseResponse();
        }
        if (extra != null) {
            body.extra = extra;
        }
        body.code = Http.ERROR_CODE_FAILURE;
        body.desc = getFailureDesc();
        EventBus.getDefault().post(body);
    }

    protected String getFailureDesc() {
        return "network error";
    }

}



