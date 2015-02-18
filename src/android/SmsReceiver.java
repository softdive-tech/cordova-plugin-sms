package com.applegrew.cordova.android.plugin.sms;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	

	public static final String SMS_EXTRA_NAME = "pdus";
	
	private Callback callback_receive;
	private boolean isReceiving = true;
	
	// This broadcast boolean is used to continue or not the message broadcast
	// to the other BroadcastReceivers waiting for an incoming SMS (like the native SMS app)
	private boolean broadcast = true;
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		// Get the SMS map from Intent
	    Bundle extras = intent.getExtras();
	    if (extras != null)
	    {
		   // Get received SMS Array
			Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

			for (int i=0; i < smsExtra.length; i++)
			{
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
				if(this.isReceiving && this.callback_receive != null) {
					JSONObject obj = new JSONObject();
					try {
                        obj.put(SMS.ADDRESS, sms.getOriginatingAddress());
                        obj.put(SMS.BODY, sms.getMessageBody());
                        obj.put(SMS.DATE, System.currentTimeMillis());
                        callback_receive.resultJsonObject(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
				}
			}

			// If the plugin is active and we don't want to broadcast to other receivers
			// Also we cannot abort a broadcast which is not ordered.
			if (this.isReceiving && !broadcast && isOrderedBroadcast()) {
				this.abortBroadcast();
			}
	     }
	}
	
	public void broadcast(boolean v) {
		this.broadcast = v;
	}
	
	protected void startReceiving(Callback ctx) {
		this.callback_receive = ctx;
		this.isReceiving = true;
	}

	public void stopReceiving() {
		this.callback_receive = null;
		this.isReceiving = false;
	}
	
}