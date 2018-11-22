package com.pdf.workguide.http;

import android.content.Context;

import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.observers.SafeSubscriber;


/**
 * Description:
 * Data: 2018/6/30
 *
 * @author: cqian
 */

public class HttpUtils {
    private static Novate mNovate;
    private static SafeSubscriber mSafeSubscriber;
    private static Context mContext;
    public static Map<String, String> headers = new HashMap<>();

    public static Novate getNovate() {
        if (mNovate == null) {
            synchronized (HttpUtils.class) {
                if (mNovate == null) {
                    headers.put("Content-Type", "application/json");
                    //headers.put("device", Constant.UUID);

                    mNovate = new Novate.Builder(mContext)
                            //.addParameters(parameters)
                            .connectTimeout(30)
                            .writeTimeout(90)
                            .baseUrl(HttpUrl.SERVER_URL)
                            .addHeader(headers)
                            .addCache(false)
                            .addLog(true)
                            .build();
                }
            }
        }
        return mNovate;
    }

    public static void init(Context context) {
        mContext = context;
    }


    public static void clearToken() {
        HttpUtils.headers.remove("Authorization");
    }

    public static void clearUuid() {
        HttpUtils.headers.remove("deviceuuid");
    }

    public static void clearTokenUuid() {
        HttpUtils.headers.remove("Authorization");
        HttpUtils.headers.remove("deviceuuid");
    }

    public static void doPost(String url, JSONObject jsonObject, final CallBack callBack) {
        LogUtils.debug("═══════════════════════════════════════url═════════════════════════════════════════════");
        LogUtils.debug(HttpUrl.SERVER_URL + url);
        LogUtils.debug("═══════════════════════════════════════headers═════════════════════════════════════════════");

        String param = "";
        if (jsonObject != null) {
            LogUtils.debug("═══════════════════════════════════════请求参数═════════════════════════════════════════════");

            param = jsonObject.toString();
            LogUtils.debug(jsonObject.toString());
        }
        Object subscriber = getNovate().json(HttpUrl.SERVER_URL + url, param, new BaseSubscriber<ResponseBody>() {

            @Override
            public void onError(Throwable e) {
                if (e != null && e.getMessage() != null) {
                    LogUtils.debug("══════════════════════════════════════Novate.Throwable═══════════════════════════════════════════");
                    LogUtils.debug(e.getMessage());
                }
                callBack.onFailed(e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                callBack.parseData(responseBody);

            }
        });

        if (subscriber instanceof SafeSubscriber) {
            mSafeSubscriber = (SafeSubscriber) subscriber;
        }


    }

    public static void cancle() {
        if (mSafeSubscriber != null) {
            mSafeSubscriber.unsubscribe();
        }
    }

    public static void doPost(String url, final CallBack callBack) {
        doPost(url, null, callBack);
    }
}
