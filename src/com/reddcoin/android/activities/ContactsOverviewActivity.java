package com.reddcoin.android.activities;

import com.reddcoin.android.R;
import com.reddcoin.android.contentproviders.ContactContentProvider;
import com.reddcoin.android.contentproviders.WithdrawalContentProvider;
import com.reddcoin.android.database.ContactTable;
import com.reddcoin.android.database.WithdrawalTable;
import com.reddcoin.android.fragments.ContactsOverviewFragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

@SuppressLint("NewApi")
public class ContactsOverviewActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int EDIT_ID = Menu.FIRST + 1;
	private static final int DELETE_ID = Menu.FIRST + 2;
	private static final int SHARE_ID = Menu.FIRST + 3;
	private SimpleCursorAdapter adapter;
	private ContactsOverviewFragment contactsOverviewFragment;
	private String address;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		address = intent.getStringExtra(MainActivity.ADDRESS);
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);
		contactsOverviewFragment = new ContactsOverviewFragment();
		contactsOverviewFragment.setContext(this);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, contactsOverviewFragment).commit();
		}
		fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contactsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:            
			super.onBackPressed();         
	         return true;
		case R.id.insert:
			createContact();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Uri uri = Uri.parse(ContactContentProvider.CONTENT_URI + "/"
					+ info.id);
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;
	    case EDIT_ID:
	    	AdapterContextMenuInfo info2 = (AdapterContextMenuInfo) item
			.getMenuInfo();
	    	startDetailActivity(info2.id);
	    	return true;
		case SHARE_ID:
			AdapterContextMenuInfo info3 = (AdapterContextMenuInfo) item
			.getMenuInfo();
			Uri uri2 = Uri.parse(ContactContentProvider.CONTENT_URI + "/"
			+ info3.id);
			String[] projection = { ContactTable.COLUMN_NAME, ContactTable.COLUMN_ADDRESS};
			Cursor cursor = getContentResolver().query(uri2, projection, null, null,
					null);
			if (cursor != null) {
				cursor.moveToFirst();
				
				String contactName = cursor.getString(cursor
						.getColumnIndexOrThrow(ContactTable.COLUMN_NAME));
				String contactAddress = cursor.getString(cursor
						.getColumnIndexOrThrow(WithdrawalTable.COLUMN_ADDRESS));
				// Always close the cursor
				cursor.close();
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "ReddCoin contact: [ Name: " + contactName + " ] [ Address: " + contactAddress + " ]");
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
			}
		}
		return super.onContextItemSelected(item);
	}

	private void createContact() {
		Intent i = new Intent(this, ContactDetailActivity.class);

		Log.d("oioi", "addresschck 1: " + address);
		i.putExtra(MainActivity.ADDRESS, address);
		startActivity(i);
	}

	private void fillData() {

		String[] from = new String[] { ContactTable.COLUMN_NAME };
		int[] to = new int[] { R.id.label };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.contacts_row, null, from,
				to, 0);

		contactsOverviewFragment.setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
		menu.add(0, DELETE_ID, 1, R.string.menu_delete);
		menu.add(0, SHARE_ID, 2, R.string.menu_share);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { ContactTable.COLUMN_ID, ContactTable.COLUMN_NAME };
		String searchQuery = ContactTable.COLUMN_OWNER + " = '" + address + "'";
		CursorLoader cursorLoader = new CursorLoader(this,
				ContactContentProvider.CONTENT_URI, projection, searchQuery, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	public void startDetailActivity(long id) {
		Intent i = new Intent(this, ContactDetailActivity.class);
		Uri contactUri = Uri.parse(ContactContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(ContactContentProvider.CONTENT_ITEM_TYPE, contactUri);
		i.putExtra(MainActivity.ADDRESS, address);
		startActivity(i);
	}
	
	public void startMainActivity(long id) {
		Uri contactUri = Uri.parse(ContactContentProvider.CONTENT_URI + "/" + id);
		String[] projection = { ContactTable.COLUMN_ADDRESS };
		Cursor cursor = getContentResolver().query(contactUri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			
			String targetAddress = cursor.getString(cursor
					.getColumnIndexOrThrow(ContactTable.COLUMN_ADDRESS));
			// Always close the cursor
			cursor.close();
			Intent intent = new Intent();
			intent.putExtra("contactaddress", targetAddress);
			setResult(RESULT_OK, intent);        
			finish();
		}
	}
}