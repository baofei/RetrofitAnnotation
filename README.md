# RetrofitAnnotation
retrofit 和 annotation Processor ，EventBus结合

用annotation processor 把Retrofit的接口生成Manager，如

public final class ApiServiceManager extends BaseManager {
private static ApiServiceManager sApiServiceManager;

public static synchronized ApiServiceManager getInstance() {
if (sApiServiceManager == null) {
sApiServiceManager = new ApiServiceManager();
}
return sApiServiceManager;
}

public Call getDefaultAddr() {
Call call = getService().getDefaultAddr();
call.enqueue(new ApCallBack(ResultBean.class));
return call;
}
}

请求成功的数据将会使用EventBus post出去
