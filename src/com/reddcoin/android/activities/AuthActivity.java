package com.reddcoin.android.activities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.os.Build;

import com.google.gson.Gson;
import com.reddcoin.android.R;
import com.reddcoin.android.ReddAPI.API;
import com.reddcoin.android.fragments.AuthFragment;
import com.reddcoin.android.fragments.PurseFragment;
import com.reddcoin.android.model.UserInfo;
import com.reddcoin.android.model.Withdrawal;
import com.reddcoin.android.qr.Contents;
import com.reddcoin.android.qr.QRCodeEncoder;
import com.reddcoin.android.tasks.checkAccountTask;
import com.reddcoin.android.tasks.createNewUserTask;
import com.reddcoin.android.tasks.getBalanceTask;
import com.reddcoin.android.tasks.getUserInfoTask;
import com.reddcoin.android.tasks.sendToAddressTask;

public class AuthActivity extends FragmentActivity {

	private API reddAPI;
	private AuthFragment authFragment;
	private String target;
	private String userName = "reddcoinpurse";
	private boolean userExists;
	
	public final static String GETKEY_MESSAGE = "GET";
	public final static String POSTKEY_MESSAGE = "POST";
	public final static String USEREXISTS_MESSAGE = "USER";
	
	public AuthActivity() {
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		//reddAPI = new API(
		//		"1Mn8CGmmD5Vna3baT15NqflXeWzgx65FGJK0nDyG5H1cj7Z25y6utYGW031qRGxG",
		//		"XP7T79vW25X4MOpQ1SgeV85C2ruhuccMRnMjcQPjVg6Bb3a2Pju503Kxs40QmO0x");
		authFragment = new AuthFragment();
		authFragment.setContext(this);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, authFragment).commit();
		}
		//getCurrentUserInfo();
		//getBalance();
	}
	
	@Override
	public void onPause()
	{
	    super.onPause();
	}

	@Override
	public void onResume()
	{
	    super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
	
	public void checkUser(String GETKey, String POSTKey) {
		reddAPI = new API(GETKey, POSTKey);
		new checkAccountTask(reddAPI, this).execute(userName);
	}
	
	public void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
	    //EditText editText = (EditText) findViewById(R.id.edit_message);
	    //String message = editText.getText().toString();
	    intent.putExtra(GETKEY_MESSAGE, reddAPI.getKey_GET());
	    intent.putExtra(POSTKEY_MESSAGE, reddAPI.getKey_POST());
	    intent.putExtra(USEREXISTS_MESSAGE, userExists);
	    startActivity(intent);
	    finish();
	}
	
	public void setQRTarget(String target) {
		this.target = target;
	}
	
	public void setUserExists(boolean userExists) {
		this.userExists = userExists;
	}
	
//	public void setBalance(double newBalance) {
//		balance = newBalance;
//		purseFragment.setBalanceValue(balance);
//	}
//	
//	public void getBalance() {
//		new getBalanceTask(reddAPI, this).execute(userName);
//	}
//	
//	public void getAddress() {
//		userInfo.getDepositAddress();
//	}
//	
//	public void getCurrentUserInfo() {
//		new getUserInfoTask(reddAPI, this).execute(userName);
//	}
//	
//	public void setCurrentUserInfo(UserInfo userInfo) {
//		this.userInfo = userInfo;
//		String address = userInfo.getDepositAddress();
//		purseFragment.setAddressValue(address);
//		generateQR(userInfo.getDepositAddress());
//	}
	
	public boolean checkForError(String jsonString) {
		if(jsonString.contains("ErrorMessage")) {
		
		return true;
		}
		return false;
	}
	
	public String getErrorFromJSON(String jsonString) {
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (jsonString, JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		return jsonObj.get("ErrorMessage").getAsString();
	}
	
	public void showErrorMessage(String message) {
		Toast.makeText(getApplicationContext(), "ERROR: " + message, Toast.LENGTH_SHORT).show();
	}
	
	public void showMessage(String message) {
		Toast.makeText(getApplicationContext(), "Message: " + message, Toast.LENGTH_SHORT).show();
	}
	
	public void startScanner() {
		
	}
	
	public void generateQR(String input) {
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(input, 
	             null, 
	             Contents.Type.TEXT,  
	             BarcodeFormat.QR_CODE.toString(), 
	             200);
	   try {
	    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
	    ImageView myImage = (ImageView) findViewById(R.id.qr_image);
	    myImage.setImageBitmap(bitmap);
	 
	   } catch (WriterException e) {
	    e.printStackTrace();
	   }
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
	    if (requestCode == 0)
	    {
	    	authFragment.enableQRButtons();
	        if (resultCode == RESULT_OK)
	        {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            if(contents != null && target != null) {
	            	if(target.equals("GET")) {
	            		authFragment.setGetKeyValue(contents);
	            	}
	            	else if(target.equals("POST")) {
	            		authFragment.setPostKeyValue(contents);
	            	}
	            }
	        }
	        else if (resultCode == RESULT_CANCELED)
	        {
	            Log.i("xZing", "Cancelled");
	        }
	    }
	}

	public void allowNewLoginAttempt() {
		authFragment.enableConnectButton();
	}
}