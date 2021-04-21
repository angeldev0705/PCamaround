package com.pcamarounds.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnection implements Interceptor {

    private Context mContext;

    public NetworkConnection(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!isOnline()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> Toast.makeText(mContext, "No Internet!", Toast.LENGTH_LONG).show(), 500);
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }


}
