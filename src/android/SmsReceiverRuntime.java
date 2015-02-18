package com.applegrew.cordova.android.plugin.sms;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

public class SmsReceiverRuntime extends SmsReceiver implements Callback {
    private CallbackContext callback_receive;
    
    public void startReceiving(CallbackContext ctx) {
        this.callback_receive = ctx;
        super.startReceiving(this);
    }

    @Override
    public void resultJsonObject(JSONObject json) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, json);
      result.setKeepCallback(true);
      callback_receive.sendPluginResult(result);
    }
    
}
