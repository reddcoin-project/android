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

public class getUserInfoTask extends AsyncTask<String, Integer, UserInfo> {
	private API reddAPI;
	private MainActivity ctx;
	private boolean errorFound = false;
	private String JSONerrorObject;
	private CustomProgressDialog dialog;
	private boolean silentMode;
	
	public getUserInfoTask(API reddAPI, MainActivity ctx, boolean _silentMode) {
		this.reddAPI = reddAPI;
		this.ctx = ctx;
		if(_silentMode) {
			silentMode = true;
		}
	}
	
	protected void onPreExecute() {
		dialog = new CustomProgressDialog(ctx);
		dialog.setMessage("Downloading user-info..");
		dialog.show();
	}

	protected UserInfo doInBackground(String... users) {
		UserInfo userInfo = null;
		if(isOnline()) {
		String infoString = reddAPI.getUserInfo(users[0]);
		if (!(ctx.checkForError(infoString))) {
			userInfo = new Gson().fromJson(infoString, UserInfo.class);
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

	protected void onPostExecute(UserInfo result) {
		if(!this.isCancelled()) {
		if(errorFound) {
			String message = ctx.getErrorFromJSON(JSONerrorObject);
			if(!silentMode)
			ctx.showErrorMessage(message);
		}
		else
			ctx.setCurrentUserInfo(result);
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
