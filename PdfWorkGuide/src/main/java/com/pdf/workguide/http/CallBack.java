package com.pdf.workguide.http;

import com.pdf.workguide.base.BaseBean;
import com.pdf.workguide.util.JsonParserUtil;
import com.tamic.novate.Throwable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.ResponseBody;

/**
 * Description:
 * Data: 2018/6/30
 *
 * @author: cqian
 */

public abstract class CallBack<T> {
    public abstract void onSuccess(T data);

    public abstract void onFailed(Throwable e);

    public String responseData;
    public void parseData(ResponseBody responseBody) {
        try {
            responseData = new String(responseBody.bytes());
            LogUtils.debug("═══════════════════════════════════════返回值═════════════════════════════════════════════");
            LogUtils.debug(responseData);
            Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            T t = JsonParserUtil.deserializeByJson(responseData,entityClass);
            if (entityClass!=null && t instanceof BaseBean){
                if ("200".equals(((BaseBean)t).state)){
                    onSuccess(t);
                }else {
                    LogUtils.debug("══════════════════════════════════════Error Message═══════════════════════════════════════════");
                    LogUtils.debug(((BaseBean)t).messageInfo);
                    Throwable e=new Throwable(new java.lang.Throwable(),((BaseBean)t).state,((BaseBean)t).messageInfo);
                    onFailed(e);
                }
            }else {
                onSuccess(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e!=null){
                LogUtils.debug("══════════════════════════════════════IOException Message═══════════════════════════════════════════");
                LogUtils.debug(e.getMessage());
            }
        }
    }

}
