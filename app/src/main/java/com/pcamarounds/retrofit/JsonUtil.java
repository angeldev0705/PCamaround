package com.pcamarounds.retrofit;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class JsonUtil {
    private static final String TAG = "JsonUtil";
    public static String STATUS = "status";
    public static String MESSAGE = "message";

    public static JSONObject mainjson(Response<ResponseBody> response) {
        JSONObject jsonObject = null;
        try {
            String resp = response.body().string().trim();
            Log.e(TAG, "mainjson: " + resp);
            jsonObject = new JSONObject(resp);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String resp(Response<ResponseBody> response) {
        try {
            String resp = response.body().string().trim();
            Log.e(TAG, "resp: " + resp);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    static void handleResponse(Response<ResponseBody> response, WebResponse webResponse) {
        if (response.isSuccessful()) {
            if (response.body() != null) {
                webResponse.onResponseSuccess(response);
            } else {
                webResponse.onResponseFailed(response.message());
            }
        } else {
            try {
                if (response.errorBody() != null) {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    webResponse.onResponseFailed(jObjError.getString("error"));
                } else {
                    webResponse.onResponseFailed(response.message());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    public static List<Class<?>> listfromjson(Class<?> cls, String data) {
//        Gson gson = new Gson();
//        List<cls> clsArrayList = new ArrayList<>();
//        gson.fromJson(data, new TypeToken<ArrayList<cls>>() {}.getType());
//        return  clsArrayList;
//    }

}
