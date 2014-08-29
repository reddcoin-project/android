package com.reddcoin.android.tasks;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.reddcoin.android.ReddAPI.API;
import com.reddcoin.android.activities.AuthActivity;
import com.reddcoin.android.activities.MainActivity;
import com.reddcoin.android.dialogs.CustomProgressDialog;
import com.reddcoin.android.model.Partner;
import com.reddcoin.android.model.Transaction;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class checkAccountTask extends AsyncTask<String, Integer, Double> {
	private API reddAPI;
	private AuthActivity ctx;
	private boolean errorFound = false;
	private String JSONerrorObject;
	private CustomProgressDialog dialog;
	
	public checkAccountTask(API reddAPI, AuthActivity ctx) {
		this.reddAPI = reddAPI;
		this.ctx = ctx;
	}
	
	protected void onPreExecute() {
		dialog = new CustomProgressDialog(ctx);
		dialog.setMessage("Verifying account..");
		dialog.show();
	}

	protected Double doInBackground(String... users) {
		double balance = 0;
		if(isOnline()) {
			String balanceString = reddAPI.getUserBalance(users[0]);
			if(balanceString != null) {
			if (!(ctx.checkForError(balanceString))) {
				balance = Double.parseDouble(balanceString);
			} 
			else {
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
			//ctx.showErrorMessage(message);
			if(message.contains("API")) {
				ctx.showMessage("Authentication failed");
				ctx.allowNewLoginAttempt();
			}
			else if(message.contains("connection")) {
				ctx.allowNewLoginAttempt();
			}
			else {
				ctx.setUserExists(false);
				ctx.startMainActivity();
			}
		}
		else {
			ctx.setUserExists(true);
			ctx.startMainActivity();
		}
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
