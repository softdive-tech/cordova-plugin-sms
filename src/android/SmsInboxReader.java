package com.applegrew.cordova.android.plugin.sms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class SmsInboxReader {

    final Uri SMS_INBOX = Uri.parse(SMS.CONTENT_URI);
    private ContentResolver resolver;

    public SmsInboxReader(ContentResolver conResolver) {
        resolver = conResolver;
    }
    
    protected JSONArray getMessages(int maxRows, String whereClause, String[] whereArgs) {
        return getMessages(maxRows, whereClause, whereArgs, null);
    }

    public JSONArray getMessages(int maxRows, String whereClause, String[] whereArgs, String orderByClause) {
        if (orderByClause == null) {
            orderByClause = SMS.DATE + " ASC";
        }
        if (maxRows > 0) {
            orderByClause += " LIMIT " + maxRows;
        }
        Cursor cur = resolver.query(SMS_INBOX, new String[] { SMS._ID,
                SMS.ADDRESS, SMS.DATE, SMS.SUBJECT, SMS.BODY }, whereClause,
                whereArgs, orderByClause);
        JSONArray arr = new JSONArray();
        while (cur != null && cur.moveToNext()) {
            JSONObject row = new JSONObject();
            try {
                row.put(SMS._ID, cur.getString(0));
                row.put(SMS.ADDRESS, cur.getString(1));
                row.put(SMS.DATE, cur.getString(2));
                //row.put(SMS.SUBJECT, cur.getString(3));
                row.put(SMS.BODY, cur.getString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            arr.put(row);
        }
        if (cur != null) {
            cur.close();
        }
        return arr;
    }
    
    public int getAllMessagesCount() {
        Cursor c = resolver.query(SMS_INBOX, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public int getAllUnreadMessagesCount() {
        Cursor c = resolver.query(SMS_INBOX, null, SMS.READ + " = 0", null, null);
        int unreadMessagesCount = c.getCount();
        c.close();
        return unreadMessagesCount;
    }

    public JSONArray getAllMessages(int maxRowsCount) {
        return getMessages(maxRowsCount, null, null);
    }

    public JSONArray getAllUnreadMessages(int maxRowsCount) {
        return getMessages(maxRowsCount, SMS.READ + " = 0", null);
    }

    public void setMessageStatusRead(String _id) {
        ContentValues values = new ContentValues();
        values.put(SMS.READ, true);
        resolver.update(SMS_INBOX, values, SMS._ID + " = ?", new String[] { _id });
    }

}
