package com.pcamarounds.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by pc6 on 3/20/2017.
 */

public interface WebResponse {

    void onResponseSuccess(Response<ResponseBody> result);

    void onResponseFailed(String error);
}