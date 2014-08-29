package com.reddcoin.android.activities;

import com.reddcoin.android.R;
import com.reddcoin.android.contentproviders.ContactContentProvider;
import com.reddcoin.android.database.ContactTable;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/*
 * TodoDetailActivity allows to enter a new contact 
 * or to change an existing one
 */
public class ContactDetailActivity extends Activity {
	private Spinner mCategory;
	private EditText mTitleText; 
	private EditText mBodyText;
	private Button qrButton;
	private String address;
	private Uri contactUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.contact_edit);
		Intent intent = getIntent();
		address = intent.getStringExtra(MainActivity.ADDRESS);
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		Log.d("oioi", "addresschck 2: " + address);
		mTitleText = (EditText) findViewById(R.id.contact_edit_name);
		mBodyText = (EditText) findViewById(R.id.contact_edit_address);
		qrButton = (Button) findViewById(R.id.qrbutton);
		Button confirmButton = (Button) findViewById(R.id.contact_edit_button);

		Bundle extras = getIntent().getExtras();

		// Check from the saved Instance
		contactUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(ContactContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			if(extras
					.getParcelable(ContactContentProvider.CONTENT_ITEM_TYPE) != null) {
			contactUri = extras
					.getParcelable(ContactContentProvider.CONTENT_ITEM_TYPE);

			fillData(contactUri);
			}
		}
		
		qrButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				qrButton.setEnabled(false);
				try {
					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
					startActivityForResult(intent, 0);
				} catch (ActivityNotFoundException e) {
					showErrorMessage("zXing Barcode Scanner not found!");
				}
			}
		});

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(mTitleText.getText().toString())) {
					makeToast("Provide in a name");
				}
				else if (TextUtils.isEmpty(mBodyText.getText().toString())) {
					makeToast("Provide an address");
				}
				else {
					setResult(RESULT_OK);
					finish();
				}
			}

		});
		
	}

	private void fillData(Uri uri) {
		String[] projection = { ContactTable.COLUMN_NAME,
				ContactTable.COLUMN_ADDRESS, ContactTable.COLUMN_OWNER };
		Log.d("oioioi", "URI: " + uri);
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();

			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ContactTable.COLUMN_NAME)));
			mBodyText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ContactTable.COLUMN_ADDRESS)));

			// Always close the cursor
			cursor.close();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(ContactContentProvider.CONTENT_ITEM_TYPE, contactUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:            
			super.onBackPressed();         
	         return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveState() {
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();

		// Only save if either summary or description
		// is available

		if (description.length() == 0 && summary.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(ContactTable.COLUMN_NAME, summary);
		values.put(ContactTable.COLUMN_ADDRESS, description);
		values.put(ContactTable.COLUMN_OWNER, address);

		if (contactUri == null) {
			// New todo
			contactUri = getContentResolver().insert(
					ContactContentProvider.CONTENT_URI, values);
		} else {
			// Update todo
			getContentResolver().update(contactUri, values, null, null);
		}
	}

	private void makeToast(String message) {
		Toast.makeText(ContactDetailActivity.this, message,
				Toast.LENGTH_LONG).show();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			enableQRButton(true);
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				if (contents != null) {
					mBodyText.setText(contents);
				}
			} else if (resultCode == RESULT_CANCELED) {
				Log.i("xZing", "Cancelled");
			}
		} 
	}
	
	private void enableQRButton(boolean enable) {
		qrButton.setEnabled(enable);
	}
	
	public void showErrorMessage(String message) {
		Toast.makeText(getApplicationContext(), "ERROR: " + message,
				Toast.LENGTH_SHORT).show();
	}
}
