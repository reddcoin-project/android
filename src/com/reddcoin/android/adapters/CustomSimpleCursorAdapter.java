package com.reddcoin.android.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.reddcoin.android.R;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {

	public CustomSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int i) {
		super(context, layout, c, from, to, i);
	}

	@Override
    public void setViewText(TextView v, String text) {
		if (v.getId() == R.id.amount) {
			text = "-" + text + " RDD";
		}
        if (v.getId() == R.id.timestamp) { 
            text = getDate(Long.parseLong(text), "yyyy-MM-dd HH:mm:ss");
        }
        v.setText(text);
    }
	
	public String getDate(Long milliSeconds, String dateFormat) {
	    Date date = new Date(milliSeconds); 
	    return date.toString();
	}
}

