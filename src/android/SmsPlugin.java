package com.applegrew.cordova.android.plugin.sms;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

public class SmsPlugin extends CordovaPlugin {
    public final String ACTION_HAS_SMS_POSSIBILITY = "HasSMSPossibility";
    public final String ACTION_RECEIVE_SMS = "StartReception";
    public final String ACTION_STOP_RECEIVE_SMS = "StopReception";

    public final String ACTION_GET_SMS_BY_CUSTOM_CRITERIA = "GetSmsByCustomCriteria";
    public final String ACTION_GET_ALL_SMS = "GetAllSms";
    public final String ACTION_GET_ALL_SMS_COUNT = "GetAllSmsCount";
    public final String ACTION_GET_ALL_UNREAD_SMS = "GetAllUnreadSms";
    public final String ACTION_GET_ALL_UNREAD_SMS_COUNT = "GetAllUnreadSmsCount";
    public final String ACTION_SET_SMS_READ = "SetSmsAsRead";

    private CallbackContext callback_receive;
    private SmsReceiverRuntime smsReceiver = null;
    private SmsInboxReader smsReader = null;
    private boolean isReceiving = false;

    public SmsPlugin() {
        super();
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        smsReader = new SmsInboxReader(cordova.getActivity()
                .getContentResolver());
    }

    @Override
    public void onDestroy() {
        stopReceiving(true);
        if (smsReader != null) {
            smsReader = null;
        }
    }

    @Override
    public boolean execute(final String action, final JSONArray arg,
            final CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_HAS_SMS_POSSIBILITY)) {

            Activity ctx = this.cordova.getActivity();
            if (ctx.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_TELEPHONY)) {
                callbackContext.sendPluginResult(new PluginResult(
                        PluginResult.Status.OK, true));
            } else {
                callbackContext.sendPluginResult(new PluginResult(
                        PluginResult.Status.OK, false));
            }
            return true;
        } else if (action.equals(ACTION_RECEIVE_SMS)) {

            // if already receiving (this case can happen if the startReception
            // is called
            // several times
            if (this.isReceiving) {
                // close the already opened callback ...
                PluginResult pluginResult = new PluginResult(
                        PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(false);
                this.callback_receive.sendPluginResult(pluginResult);

                // ... before registering a new one to the sms receiver
            }
            this.isReceiving = true;

            if (this.smsReceiver == null) {
                this.smsReceiver = new SmsReceiverRuntime();
                IntentFilter fp = new IntentFilter(
                        "android.provider.Telephony.SMS_RECEIVED");
                fp.setPriority(1000);
                // fp.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                this.cordova.getActivity().registerReceiver(this.smsReceiver,
                        fp);
            }

            this.smsReceiver.startReceiving(callbackContext);

            PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            this.callback_receive = callbackContext;

            //final String whereClause = JSONObject.NULL.equals(obj) ? null : obj;
            return true;
        } else if (action.equals(ACTION_STOP_RECEIVE_SMS)) {

            stopReceiving(false);

            // 1. Stop the receiving context
            PluginResult pluginResult = new PluginResult(
                    PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(false);
            this.callback_receive.sendPluginResult(pluginResult);

            // 2. Send result for the current context
            pluginResult = new PluginResult(PluginResult.Status.OK);
            callbackContext.sendPluginResult(pluginResult);

            return true;
        } else if (ACTION_GET_SMS_BY_CUSTOM_CRITERIA.equals(action)) {
            final int maxRows = arg.getInt(0);
            String obj = arg.getString(1);
            final String[] whereArgs = JSONUtil.jsonArrayToStringArray(arg.getJSONArray(2));
            obj = arg.getString(3);
            final String orderByClause = JSONObject.NULL.equals(obj) ? null : obj;
            
            cordova.getThreadPool().execute(new Runnable () {
                @Override
                public void run() {
                    //callbackContext.success(smsReader.getMessages(maxRows, whereClause, whereArgs, orderByClause));
                    callbackContext.success(smsReader.getMessages(maxRows, null, whereArgs, orderByClause));
                }
            });
            return true;
        } else if (ACTION_GET_ALL_SMS.equals(action)) {
            final int maxRows = arg.getInt(0);
            cordova.getThreadPool().execute(new Runnable () {
                @Override
                public void run() {
                    callbackContext.success(smsReader.getAllMessages(maxRows));
                }
            });
            return true;
        }  else if (ACTION_GET_ALL_SMS_COUNT.equals(action)) {
            cordova.getThreadPool().execute(new Runnable () {
                @Override
                public void run() {
                    callbackContext.success(smsReader.getAllMessagesCount());
                }
            });
            return true;
        } else if (ACTION_GET_ALL_UNREAD_SMS.equals(action)) {
            final int maxRows = arg.getInt(0);
            cordova.getThreadPool().execute(new Runnable () {
                @Override
                public void run() {
                    callbackContext.success(smsReader.getAllUnreadMessages(maxRows));
                }
            });
            return true;
        }  else if (ACTION_GET_ALL_UNREAD_SMS_COUNT.equals(action)) {
            cordova.getThreadPool().execute(new Runnable () {
                @Override
                public void run() {
                    callbackContext.success(smsReader.getAllUnreadMessagesCount());
                }
            });
            return true;
        } else if (ACTION_SET_SMS_READ.equals(action)) {
            final String id = arg.getString(0);
            cordova.getThreadPool().execute(new Runnable () {
                @Override
                public void run() {
                    smsReader.setMessageStatusRead(id);
                    callbackContext.success();
                }
            });
            return true;
        }

        return false;
    }
    
    private void stopReceiving(boolean alsoUnregister) {
        if (this.smsReceiver != null) {
            smsReceiver.stopReceiving();
            if (alsoUnregister) {
                cordova.getActivity().unregisterReceiver(smsReceiver);
                this.smsReceiver = null;
            }
        }
        this.isReceiving = false;
    }

}
