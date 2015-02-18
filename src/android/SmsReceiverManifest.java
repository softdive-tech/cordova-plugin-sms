package com.applegrew.cordova.android.plugin.sms;

import org.json.JSONObject;

public abstract class SmsReceiverManifest extends SmsReceiver implements Callback {
    public SmsReceiverManifest() {
        super.startReceiving(this);
    }

    /**
     * This method is invoked by SmsReceiver.onReceive() for each SMS
     * it receives. Usually that would be one call to this per one
     * call to onReceive() by the system.
     * 
     * Make sure NOT TO launch threads from this method. EVERY action
     * must be completed here synchronously, since Android would be
     * free to KILL this process once the onReceive() of SmsReceiver
     * returns.
     */
    @Override
    abstract public void resultJsonObject(JSONObject json);

}
