package com.reddcoin.android.tasks;

import java.net.URL;

import com.google.gson.Gson;
import com.reddcoin.android.ReddAPI.API;
import com.reddcoin.android.activities.MainActivity;
import com.reddcoin.android.contentproviders.WithdrawalContentProvider;
import com.reddcoin.android.database.WithdrawalTable;
import com.reddcoin.android.dialogs.CustomProgressDialog;
import com.reddcoin.android.handlers.WithdrawalsDatabaseHandler;
import com.reddcoin.android.model.UserInfo;
import com.reddcoin.android.model.Withdrawal;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class sendToAddressTask extends AsyncTask<Withdrawal, Integer, String> {
	private API reddAPI;
	private MainActivity ctx;
	private boolean errorFound = false;
	private String JSONerrorObject;
	private Withdrawal currentWithdrawal;
	private CustomProgressDialog dialog;
	
	public sendToAddressTask(API reddAPI, MainActivity ctx) {
		this.reddAPI = reddAPI;
		this.ctx = ctx;
	}
	
	protected void onPreExecute() {
		dialog = new CustomProgressDialog(ctx);
		dialog.setMessage("Processing transaction..");
		dialog.show();
	}

	protected String doInBackground(Withdrawal... tx) {
		String txStatus = null;
		if(isOnline()) {
		currentWithdrawal = tx[0];
		String infoString = reddAPI.sendToAddress(tx[0].getUserName(), tx[0].getAddress(), tx[0].getAmount());
		if (!(ctx.checkForError(infoString))) {
			txStatus = infoString;
		} else {
			JSONerrorObject = infoString;
			errorFound = true;
		}
		}
		else {
			errorFound = true;
			JSONerrorObject = "{'ErrorMessage':'No connection available!'}";
		}
		return txStatus;
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(String result) {
		if(errorFound) {
			String message = ctx.getErrorFromJSON(JSONerrorObject);
			ctx.showErrorMessage(message);
		}
		else {
			//ctx.showMessage(result);
			currentWithdrawal.setTxid(result.replace("\"", ""));
			ctx.showTransactionResult(currentWithdrawal);
			ContentValues values = new ContentValues();
			values.put(WithdrawalTable.COLUMN_AMOUNT, currentWithdrawal.getAmount());
			values.put(WithdrawalTable.COLUMN_ADDRESS, currentWithdrawal.getAddress());
			values.put(WithdrawalTable.COLUMN_OWNER, currentWithdrawal.getOwner());
			values.put(WithdrawalTable.COLUMN_TXID, result.replace("\"", ""));
			values.put(WithdrawalTable.COLUMN_TIMESTAMP, System.currentTimeMillis());
			ctx.getContentResolver().insert(
					WithdrawalContentProvider.CONTENT_URI, values);
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
