package com.baofei.library.retrofit.manager;

import com.baofei.library.retrofit.RetrofitManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 基类，用于创建接口相关service
 * Created by baofei on 2016/9/22.
 */
public class BaseManager<T> {

    private int mRetrofitCode;

    private T service;

    public BaseManager() {
        create();
    }


    private void create() {
        mRetrofitCode = RetrofitManager.getInstance().getRetrofitCode();
        service =  (T) RetrofitManager.getInstance().getRetrofit().create(getT());
    }

    private Class getT() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) params[0];
    }

    /**
     * 获取T相关接口service类
     *
     * @return T的具体实现
     */
    public T getService() {
        if(mRetrofitCode != RetrofitManager.getInstance().getRetrofitCode()){
            resetService();
        }
        return service;
    }

    /**
     * 切换环境重置service
     */
    public void resetService() {
        create();
    }
}
