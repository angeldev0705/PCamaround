package com.pcamarounds.retrofit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ResponseStatus implements Interceptor {

    private Context mContext;
    public ResponseStatus(Context context) {
        mContext = context;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> mainresp(mContext,response), 500);
        return response;
    }

    public void  mainresp(Context context, Response response){
        switch (response.code()) {
            case 401:
                Toast.makeText(context, "Unauthorized error", Toast.LENGTH_SHORT).show();
                break;
            case 404:
                Toast.makeText(context, "Page Not Found Error", Toast.LENGTH_SHORT).show();
                break;
            case 500:
                Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT).show();
                break;
            case 504:
                Toast.makeText(context, "Gateway Timeout error", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
