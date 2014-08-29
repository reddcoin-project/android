package com.reddcoin.android.tasks;

import java.net.URL;

import com.reddcoin.android.ReddAPI.API;
import com.reddcoin.android.activities.MainActivity;
import com.reddcoin.android.dialogs.CustomProgressDialog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class getBalanceTask extends AsyncTask<String, Integer, Double> {
	private API reddAPI;
	private MainActivity ctx;
	private boolean errorFound = false;
	private String JSONerrorObject;
	private CustomProgressDialog dialog;
	private boolean silentMode = false;
	
	public getBalanceTask(API reddAPI, MainActivity ctx, boolean _silentMode) {
		this.reddAPI = reddAPI;
		this.ctx = ctx;
		if(_silentMode) {
			silentMode = true;
		}
	}

	protected void onPreExecute() {
		if(!silentMode) {
		dialog = new CustomProgressDialog(ctx);
		dialog.setMessage("Checking balance..");
		dialog.show();
		}
	}
	
	protected Double doInBackground(String... users) {
		double balance = 0;
		if(isOnline()) {
		String balanceString = reddAPI.getUserBalance(users[0]);
		if(balanceString != null) {
		if (!(ctx.checkForError(balanceString))) {
			balance = Double.parseDouble(balanceString);
		} else {
			JSONerrorObject = balanceString;
			errorFound = true;
		}
		}
		}
		else {
			errorFound = true;
			JSONerrorObject = "{'ErrorMessage':'No connection available!'}";
		}
		return balance;
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(Double result) {
		if(errorFound) {
			String message = ctx.getErrorFromJSON(JSONerrorObject);
			if(!silentMode)
			ctx.showErrorMessage(message);
		}
		else {
			ctx.setBalance(result);
		}
		if(!silentMode)
		dialog.dismiss();
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
}
