package com.reddcoin.android.fragments;

import com.reddcoin.android.R;
import com.reddcoin.android.activities.AuthActivity;
import com.reddcoin.android.activities.MainActivity;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AuthFragment extends android.support.v4.app.Fragment {

	private EditText getKeyValue; 
	private EditText postKeyValue;
	
	private Button connectButton;
	private Button registerButton;
	
	private AuthActivity ctx;
	private Button getKeyQRButton;
	private Button postKeyQRButton;

	public AuthFragment () {
	}
	
	public void setContext(AuthActivity ctx) {
		this.ctx = ctx;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_auth, container,
				false);

		getKeyValue = (EditText) rootView.findViewById(R.id.getkey_value); 
		postKeyValue = (EditText) rootView.findViewById(R.id.postkey_value);
		getKeyQRButton = (Button) rootView.findViewById(R.id.qr_getkey);
		postKeyQRButton = (Button) rootView.findViewById(R.id.qr_postkey);
		getKeyQRButton.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
		    	disableQRButtons();
		    	try {
		    	Intent intent = new Intent(
		                "com.google.zxing.client.android.SCAN");
		        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		        ctx.setQRTarget("GET");
		        ctx.startActivityForResult(intent, 0);
		    	}
		    	catch (ActivityNotFoundException e) {
		    		ctx.showErrorMessage("zXing Barcode Scanner not found!");
		    	}
		    }
		});
		postKeyQRButton.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
		    	disableQRButtons();
		    	try {
		    	Intent intent = new Intent(
		                "com.google.zxing.client.android.SCAN");
		        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		        ctx.setQRTarget("POST");
		        ctx.startActivityForResult(intent, 0);
		    	}
		    	catch (ActivityNotFoundException e) {
		    		ctx.showErrorMessage("zXing Barcode Scanner not found!");
		    	}
		    }
		});
		
		connectButton = (Button) rootView.findViewById(R.id.connect_button);
		connectButton.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
		    	String GETKey = getKeyValue.getText().toString();
		    	String POSTKey = postKeyValue.getText().toString();
		    	if(!GETKey.equals("") && !POSTKey.equals("")) {
		    	ctx.checkUser(GETKey, POSTKey);
		    	connectButton.setEnabled(false);
		    	}
		    }
		});
		
		registerButton = (Button) rootView.findViewById(R.id.register_button);
		registerButton.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
		    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddapi.com/CreateAccount"));
		    	startActivity(browserIntent);
		    }
		});
		return rootView;
	}
	
	public void setGetKeyValue(String key) {
		getKeyValue.setText("" + key);
	}
	
	public void setPostKeyValue(String key) {
		postKeyValue.setText("" + key);
	}
	
	public void enableConnectButton() {
		connectButton.setEnabled(true);
	}
	
	public void disableQRButtons() {
		getKeyQRButton.setEnabled(false);
		postKeyQRButton.setEnabled(false);
	}

	public void enableQRButtons() {
		getKeyQRButton.setEnabled(true);
		postKeyQRButton.setEnabled(true);
	}
}

