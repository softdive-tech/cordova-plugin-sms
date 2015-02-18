package com.applegrew.cordova.android.plugin.sms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

    public static String[] jsonArrayToStringArray(JSONArray jarr) throws JSONException {
        jarr = JSONObject.NULL.equals(jarr) ? null : jarr;
        
        final String[] arr = jarr == null ? null : new String[jarr.length()];
        if (jarr != null) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = jarr.getString(i);
            }
        }
        return  arr;
    }
    
}
