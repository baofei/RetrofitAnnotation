package com.baofei.retrofitannotation.net;

import com.baofei.annotation.Manager;
import com.baofei.retrofitannotation.bean.ResultBean;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by pc on 2017-03-11.
 */
@Manager
public interface ApiService {

    @POST("/api/addr/getDefaultAddr")
    Call<ResultBean> getDefaultAddr();
}
