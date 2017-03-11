package com.baofei.library.callback;


/**
 * Created by thinkpad on 2016/8/26.
 */
public class BaseResponse {

    public int code;
    public String desc;

    public Object extra;

    public boolean isEquals(Object extra) {
        if (extra == null) {
            return false;
        }
        return this.extra != null ? this.extra.equals(extra) : false;
    }

    /**
     * 是否返回成功code
     *
     * @return
     */
    public boolean isSuccessCode() {
        return code == Http.ERROR_CODE_SUCCESS;
    }

    /**
     * 是否返回错误code
     *
     * @return
     */
    public boolean isErrorCode() {
        return code == Http.ERROR_CODE_FAILURE;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", extra=" + extra +
                '}';
    }
}
