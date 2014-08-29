package com.reddcoin.android.activities;

import com.reddcoin.android.R;
import com.reddcoin.android.adapters.CustomSimpleCursorAdapter;
import com.reddcoin.android.contentproviders.ContactContentProvider;
import com.reddcoin.android.contentproviders.WithdrawalContentProvider;
import com.reddcoin.android.database.ContactTable;
import com.reddcoin.android.database.WithdrawalTable;
import com.reddcoin.android.fragments.WithdrawalsOverviewFragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.Context;
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
public class WithdrawalsOverviewActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int COPYTX_ID = Menu.FIRST + 1;
	private static final int OPENEXPLORER_ID = Menu.FIRST + 2;
	private SimpleCursorAdapter adapter;
	private String address;
	private WithdrawalsOverviewFragment withdrawalsOverviewFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		address = intent.getStringExtra(MainActivity.ADDRESS);
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);
		withdrawalsOverviewFragment = new WithdrawalsOverviewFragment();
		withdrawalsOverviewFragment.setContext(this);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, withdrawalsOverviewFragment).commit();
		}
		fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transactionsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {            
			super.onBackPressed();    
			return true;
		}
		if (id == R.id.action_deposits) {
			Intent intent = new Intent(this, DepositsOverviewActivity.class);
		    intent.putExtra(MainActivity.ADDRESS, address);
		    startActivity(intent);
		    finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPENEXPLORER_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
			Uri uri = Uri.parse(WithdrawalContentProvider.CONTENT_URI + "/"
			+ info.id);
			String[] projection = { WithdrawalTable.COLUMN_TXID };
			Cursor cursor = getContentResolver().query(uri, projection, null, null,
					null);
			if (cursor != null) {
				cursor.moveToFirst();
				
				String txID = cursor.getString(cursor
						.getColumnIndexOrThrow(WithdrawalTable.COLUMN_TXID));
				// Always close the cursor
				cursor.close();
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bitinfocharts.com/reddcoin/tx/" + txID));
				startActivity(browserIntent);
			}
			
		break;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {

		String[] from = new String[] { WithdrawalTable.COLUMN_AMOUNT, WithdrawalTable.COLUMN_ADDRESS, WithdrawalTable.COLUMN_TXID, WithdrawalTable.COLUMN_TIMESTAMP};

		int[] to = new int[] { R.id.amount, R.id.address, R.id.txid, R.id.timestamp };

		getLoaderManager().initLoader(0, null, this);
		adapter = new CustomSimpleCursorAdapter(this, R.layout.withdrawals_row, null, from,
				to, 0);

		withdrawalsOverviewFragment.setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//menu.add(0, COPYTX_ID, 0, R.string.menu_copytx);
		menu.add(0, OPENEXPLORER_ID, 0, R.string.menu_openexplorer);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { WithdrawalTable.COLUMN_ID, WithdrawalTable.COLUMN_AMOUNT, WithdrawalTable.COLUMN_ADDRESS, WithdrawalTable.COLUMN_OWNER, WithdrawalTable.COLUMN_TXID, WithdrawalTable.COLUMN_TIMESTAMP};
		String searchQuery = ContactTable.COLUMN_OWNER + " = '" + address + "'";
		CursorLoader cursorLoader = new CursorLoader(this,
				WithdrawalContentProvider.CONTENT_URI, projection, searchQuery, null, null);
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
		Uri withdrawalUri = Uri.parse(ContactContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(ContactContentProvider.CONTENT_ITEM_TYPE, withdrawalUri);
		startActivity(i);
	}
}