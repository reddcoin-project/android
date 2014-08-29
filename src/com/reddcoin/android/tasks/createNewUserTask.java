package com.reddcoin.android.tasks;

import java.net.URL;

import com.google.gson.Gson;
import com.reddcoin.android.ReddAPI.API;
import com.reddcoin.android.activities.MainActivity;
import com.reddcoin.android.dialogs.CustomProgressDialog;
import com.reddcoin.android.model.UserInfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class createNewUserTask extends AsyncTask<String, Integer, String> {
	private API reddAPI;
	private MainActivity ctx;
	private boolean errorFound = false;
	private String JSONerrorObject;
	private CustomProgressDialog dialog;
	
	public createNewUserTask(API reddAPI, MainActivity ctx) {
		this.reddAPI = reddAPI;
		this.ctx = ctx;
	}
	
	protected void onPreExecute() {
		dialog = new CustomProgressDialog(ctx);
		dialog.setMessage("Creating user..");
		dialog.show();
	}

	protected String doInBackground(String... users) {
		String userInfo = null;
		if(isOnline()) {
		String infoString = reddAPI.createNewUser(users[0]);
		if (!(ctx.checkForError(infoString))) {
			userInfo = infoString;
		} else {
			JSONerrorObject = infoString;
			errorFound = true;
		}
		}
		else {
			errorFound = true;
			JSONerrorObject = "{'ErrorMessage':'No connection available!'}";
		}
		return userInfo;
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(String result) {
		if(errorFound) {
			String message = ctx.getErrorFromJSON(JSONerrorObject);
			ctx.showErrorMessage(message);
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
