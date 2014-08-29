package com.reddcoin.android.activities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.reddcoin.android.R;
import com.reddcoin.android.ReddAPI.API;
import com.reddcoin.android.fragments.PurseFragment;
import com.reddcoin.android.handlers.UIUpdater;
import com.reddcoin.android.model.UserInfo;
import com.reddcoin.android.model.Withdrawal;
import com.reddcoin.android.qr.Contents;
import com.reddcoin.android.qr.QRCodeEncoder;
import com.reddcoin.android.tasks.createNewUserTask;
import com.reddcoin.android.tasks.getBalanceTask;
import com.reddcoin.android.tasks.getUserInfoTask;
import com.reddcoin.android.tasks.sendToAddressTask;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	public static final String ADDRESS = "address";
	private API reddAPI;
	private String userName = "reddcoinpurse";
	private static double balance;
	private UserInfo userInfo;
	private PurseFragment purseFragment;
	private String GETKey;
	private String POSTKey;
	private boolean userExists;
	private UIUpdater mUIUpdater;
	private getBalanceTask balanceTask;
	private getUserInfoTask userInfoTask;
	private ImageView qrImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		GETKey = intent.getStringExtra(AuthActivity.GETKEY_MESSAGE);
		POSTKey = intent.getStringExtra(AuthActivity.POSTKEY_MESSAGE);
		userExists = intent.getBooleanExtra(AuthActivity.USEREXISTS_MESSAGE,
				false);

		reddAPI = new API(GETKey, POSTKey);
		purseFragment = new PurseFragment();
		purseFragment.setContext(this);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, purseFragment).commit();
		}

		if (!userExists) {
			new createNewUserTask(reddAPI, this).execute(userName);
		}

		mUIUpdater = new UIUpdater(new Runnable() {
			@Override
			public void run() {
				if (userInfo == null) {
					getCurrentUserInfo(true);
				}
				getBalance(true);
			}
		});
		getCurrentUserInfo(false);
		mUIUpdater.startUpdates();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		userInfoTask.cancel(true);
		balanceTask.cancel(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_home) {
		} else if (id == R.id.action_contacts) {
			Intent intent = new Intent(this, ContactsOverviewActivity.class);
			intent.putExtra(ADDRESS, userInfo.getDepositAddress());
			startActivityForResult(intent, 1);
		} else if (id == R.id.action_transactions) {
			Intent intent = new Intent(this, DepositsOverviewActivity.class);
			intent.putExtra(ADDRESS, userInfo.getDepositAddress());
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void setBalance(double newBalance) {
		balance = newBalance;
		purseFragment.setBalanceValue(balance);
	}

	public void getBalance(boolean silentMode) {
		balanceTask = new getBalanceTask(reddAPI, this, silentMode);
		balanceTask.execute(userName);
	}

	public void getAddress() {
		userInfo.getDepositAddress();
	}

	public void getCurrentUserInfo(boolean silentMode) {
		userInfoTask = new getUserInfoTask(reddAPI, this, silentMode);
		userInfoTask.execute(userName);
	}

	public void setCurrentUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
		String address = userInfo.getDepositAddress();
		if (purseFragment != null && address != null) {
			purseFragment.setAddressValue(address);
		}
		qrImage = (ImageView) purseFragment.getQRImageView();
		if (qrImage.getDrawable() == null && userInfo != null) {
			generateQR(userInfo.getDepositAddress());
		}
	}

	public boolean checkForError(String jsonString) {
		if (jsonString.contains("ErrorMessage")) {

			return true;
		}
		return false;
	}

	public String getErrorFromJSON(String jsonString) {
		Gson gson = new Gson();
		JsonElement element = gson.fromJson(jsonString, JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		return jsonObj.get("ErrorMessage").getAsString();
	}

	public void showErrorMessage(String message) {
		Toast.makeText(getApplicationContext(), "ERROR: " + message,
				Toast.LENGTH_SHORT).show();
	}

	public void showMessage(String message) {
		Toast.makeText(getApplicationContext(), "Message: " + message,
				Toast.LENGTH_SHORT).show();
	}

	public void startScanner() {

	}

	public void startTransaction(String address, double amount) {
		Withdrawal tx = new Withdrawal(0, userName, address,
				userInfo.getDepositAddress(), "", amount, 0);
		new sendToAddressTask(reddAPI, this).execute(tx);
	}

	public void generateQR(String input) {
		if (input != null) {
			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(input, null,
					Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 150);
			try {
				Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
				qrImage = (ImageView) purseFragment.getQRImageView();
				qrImage.setImageBitmap(bitmap);

			} catch (WriterException e) {
				e.printStackTrace();
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			purseFragment.enableQRButton();
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				if (contents != null) {
					purseFragment.setAddressFieldValue(contents);
				}
			} else if (resultCode == RESULT_CANCELED) {
				Log.i("xZing", "Cancelled");
			}
		} else if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				purseFragment.setAddressFieldValue(intent
						.getStringExtra("contactaddress"));
				showMessage("Receiving address set to selected contact");
			}
		}
	}

	public void showTransactionResult(final Withdrawal completedWithdrawal) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MainActivity.this);

		alertDialogBuilder.setTitle("Transaction-details");
		alertDialogBuilder.setMessage("Transaction processed.\n\n" + "Sent:\n"
				+ completedWithdrawal.getAmount() + " RDD\nto:\n"
				+ completedWithdrawal.getAddress() + "\nfrom:\n"
				+ completedWithdrawal.getOwner() + "\n\nwith TX-ID:\n"
				+ completedWithdrawal.getTxid());
		alertDialogBuilder.setPositiveButton("Share", null);
		alertDialogBuilder.setNeutralButton("Copy TX-ID", null);
		alertDialogBuilder.setNegativeButton("Close",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) { //
				Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent sendIntent = new Intent();
								sendIntent.setAction(Intent.ACTION_SEND);
								sendIntent.putExtra(
										Intent.EXTRA_TEXT,
										"I just sent "
												+ completedWithdrawal
														.getAmount()
												+ " RDD to "
												+ completedWithdrawal
														.getAddress()
												+ " (TX-ID: "
												+ completedWithdrawal.getTxid()
												+ ")");
								sendIntent.setType("text/plain");
								startActivity(sendIntent);
							}
						});
				Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
				neutralButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								String text = completedWithdrawal.getTxid();
								if (text.length() > 0) {
									showMessage("TX-ID copied to clipboard");
									if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
										android.text.ClipboardManager clipboardMgr = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
										clipboardMgr.setText(text);
									} else {
										android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
										ClipData clip = ClipData.newPlainText(
												"Copied text", text);
										clipboardMgr.setPrimaryClip(clip);
									}
								}
							}
						});
			}
		});
		alertDialog.show();
	}
}