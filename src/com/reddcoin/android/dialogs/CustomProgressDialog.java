package com.reddcoin.android.dialogs;

import com.reddcoin.android.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class CustomProgressDialog extends ProgressDialog {
	
	private String message;

	public CustomProgressDialog(Context ctx) {
		super(ctx);
		setIndeterminate(true);
		setCancelable(false);
	}
	
	public void setMessage(String text) {
		message = text;
	}
	
	@Override
    public void show() {
     super.show();
     setContentView(R.layout.progress_dialog);
     ((TextView) findViewById(R.id.text)).setText(message);
    }
}


