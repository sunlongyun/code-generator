package com.lianshang.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * json工具类
 */
@Slf4j
public class JsonUtils {
  private static Gson gson = (new GsonBuilder ()).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
  private static JsonParser jsonParse = new JsonParser();

  public JsonUtils() {
  }

  public static JsonElement string2Json(String s) {
    try {
      return jsonParse.parse(s);
    } catch (Exception var2) {
      log.warn("Parse string 2 json error, s=" + s, var2);
      return null;
    }
  }

  public static String object2JsonString(Object obj) {
    return obj == null ? null : gson.toJson(obj);
  }

  public static <T> T json2Object(JsonElement json, Class<T> clazz) {
    try {
      return gson.fromJson(json, clazz);
    } catch (Exception var3) {
      log.warn("Parse json 2 object error, json=" + json.toString() + ", class=" + clazz.getSimpleName(), var3);
      return null;
    }
  }

  public static <T> T json2Object(String json, Class<T> clazz) {
    try {
      return gson.fromJson(json, clazz);
    } catch (Exception var3) {
      log.warn("Parse json 2 object error, json=" + json + ", class=" + clazz.getSimpleName(), var3);
      return null;
    }
  }

  public static <T> T json2Object(JsonElement json, Type type) {
    try {
      return gson.fromJson(json, type);
    } catch (Exception var3) {
      log.warn("Parse json 2 object list error, json=" + json + ", type=" + type.toString(), var3);
      return null;
    }
  }

  public static <T> T json2Object(String json, Type type) {
    try {
      return gson.fromJson(json, type);
    } catch (Exception var3) {
      log.warn("Parse json 2 object list error, json=" + json + ", type=" + type.toString(), var3);
      return null;
    }
  }
}
