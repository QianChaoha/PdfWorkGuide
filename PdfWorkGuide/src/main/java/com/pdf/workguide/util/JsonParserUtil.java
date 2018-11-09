package com.pdf.workguide.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;

public class JsonParserUtil {
	public static Gson gson = new Gson();

	public static <T> T deserializeByJson(String data,Type type) {
		if (!TextUtils.isEmpty(data)) {
			return gson.fromJson(data, type);
		}
		return null;
	}

	public static <T> T deserializeByJson(String data,Class<T> clz) {
		if (!TextUtils.isEmpty(data)) {
			return gson.fromJson(data, clz);
		}
		return null;
	}

	public static <T> String serializeToJson(T t) {
		if (t == null) {
			return "";
		}
		return gson.toJson(t);
	}


	public static String parseIatResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				// 转写结果词，默认使用第一个结果
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}


}
