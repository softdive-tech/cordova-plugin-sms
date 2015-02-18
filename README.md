Phonegap-SMS-Reception-Plugin+
==============================
By AppleGrew (Based on code by Pierre-Yves Orban)

SMS reception plugin for Phonegap (Cordova)
===========================================
This Android Phonegap plugin allows you to receive incoming SMS. You have the possibility to stop the message broadcasting and, thus, avoid the incoming message native popup.

This plugin was successfully tested with Phonegap 3.0 and Android 4.2.2 (on Samsung Galaxy Nexus and Sony Xperia Mini Pro devices).

## Adding this plugin to your project ##
0. (Make sure you are using Phonegap > 2.0)
1. Move SmsPlugin.js to your project's www folder and include a reference to it in your html files.
2. Add the java files from src to your project's src hierarchy
3. Reference the plugin in your res/config.xml file:

```
  <feature name="SmsPlugin">
    <param name="android-package" value="com.applegrew.cordova.android.plugin.sms.SmsPlugin" />
  </feature>
```
4. Ensure that your manifest contains the necessary permissions to read SMS messages:

```
  <uses-permission android:name="android.permission.RECEIVE_SMS" />
  <uses-permission android:name="android.permission.READ_SMS" />
```

## Using the plugin ##
To instantiate the plugin object:
```javascript
var smsPlugin = cordova.require('cordova/plugin/smsplugin');
```

### isSupported ###
Check if the SMS technology is supported by the device.

Example:
```javascript
smsPlugin.isSupported ((function(supported) {
  if(supported)
    alert("SMS supported !");
  else
    alert("SMS not supported");
}), function() {
  alert("Error while checking the SMS support");
});
```

### startReception (broadcast api) ###
Start the SMS receiver waiting for incoming message.
The success callback function will be called everytime a new message is received.
The success callback should expect to receive an object with the following fields - `smsPlugin.COLS.ADDRESS`,
`smsPlugin.COLS.BODY` and `smsPlugin.COLS.DATE`.
The error callback is called if an error occurs.

Example:
```javascript
smsPlugin.startReception (function(msg) {
  alert(msg[smsPlugin.COLS.BODY]);
}, function() {
  alert("Error while receiving messages");
});
```

### stopReception (broadcast api) ###
Stop the SMS receiver

Example:
```javascript
smsPlugin.stopReception (function() {
  alert("Correctly stopped");
}, function() {
  alert("Error while stopping the SMS receiver");
});
```

### getSmsByCustomCriteria (stored sms reader api) ###
Gets sms messages stored on the device based on your custom criteria.
Use the columns names mentioned in SmsPlugin.COLS.

* _maxRowsCount_ The maximum number of rows to fetch. If not needed this can be -1.
* _whereClause_ A string with the WHERE-clause, (w/o the WHERE keyword). If not needed this can be null.
* _whereArgsArray_ If any '?' is used in the above WHERE-clause then supply the value for that in this array. If not needed this can be null.

**Note:** The `msg` objects passed here and the rest APIs have one additional field - `smsPlugin.COLS.DATE`.

Example:
```javascript
// Fetches all unread SMS messages.
smsPlugin.getSmsByCustomCriteria(-1, smsPlugin.COLS.READ + '=0', null,function (msg) {
  alert(msg[smsPlugin.COLS.BODY]);
}, function() {
  alert("Error while reading SMS");
});
```

### getAllSms (stored sms reader api) ###
Gets all sms stored on the device.

* _maxRowsCount_ The maximum number of rows to fetch.

### getAllSmsCount (stored sms reader api) ###
Gets the count of all sms stored on the device.

### getAllUnreadSms (stored sms reader api) ###
Gets all unread sms stored on the device.

* _maxRowsCount_ The maximum number of rows to fetch.

### getAllUnreadSmsCount (stored sms reader api) ###
Gets the count of all unread sms stored on the device.

### setSmsAsRead (stored sms reader api) ###
Sets a given stored sms as read on the device. Note you need android.permission.WRITE_SMS permission for this. 

* _smsId_ The value of _id column of the sms.

### Aborting a broadcast ###
If you abort the broadcast using this plugin (by setting the broadcast variable to true in the `SmsReceiverRuntime`), the SMS will not be broadcast to other applications like the native SMS app. So ... be careful !

A good way to manage this is to stop the sms reception when the onPause event is fired and, when the onResume event is fired, restart the reception.

### Always capture broadcasts ###

The above broadcast APIs only works when your app is in foreground. There is another way available which works even when the app is stopped. That is called Broadcast Component. To use that you need to make the following entry inside your manifest file's `<application>`.
```
<receiver
  android:name="com.applegrew.cordova.android.plugin.sms.SampleLogSmsReceiverManifest"
  android:enabled="true"
  android:exported="true">
  <intent-filter>
      <action android:name="android.provider.Telephony.SMS_RECEIVED" />
  </intent-filter> 
</receiver>
```
When this class is invoked the activity may not have been instantiated so you need to write your own native implementation to store the broadcasts by sub-classing `SmsReceiverManifest`. The above sample simple logs them.

## Licence ##

The MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
