package com.reddcoin.android.activities;

import java.util.ArrayList;

import com.reddcoin.android.R;
import com.reddcoin.android.fragments.DepositsOverviewFragment;
import com.reddcoin.android.model.Transaction;
import com.reddcoin.android.tasks.getDepositsTask;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DepositsOverviewActivity extends FragmentActivity {
	
	private DepositsOverviewFragment depositsFragment;
	private String address;

	public DepositsOverviewActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		address = intent.getStringExtra(MainActivity.ADDRESS);
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);

		depositsFragment = new DepositsOverviewFragment();
		depositsFragment.setContext(this);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, depositsFragment).commit();
		}
		getTransactions();
	}
	
	private void getTransactions() {
		new getDepositsTask(this).execute(address);
	}
	
	public void transactionsCallback(ArrayList<Transaction> txList) {
		depositsFragment.populateTransactionList(txList);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.transactionsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {            
			super.onBackPressed();    
			return true;
		}
		if (id == R.id.action_withdrawals) {
			Intent intent = new Intent(this, WithdrawalsOverviewActivity.class);
		    intent.putExtra(MainActivity.ADDRESS, address);
		    startActivity(intent);
		    finish();
		    return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void getTransactions(String address) {
		new getDepositsTask(this).execute(address);
	}
	
	public void showErrorMessage(String message) {
		Toast.makeText(getApplicationContext(), "ERROR: " + message, Toast.LENGTH_SHORT).show();
	}
	
	public void showMessage(String message) {
		Toast.makeText(getApplicationContext(), "Message: " + message, Toast.LENGTH_SHORT).show();
	}

	public void showIsEmptyMessage() {
		depositsFragment.showIsEmpty();
	}


}