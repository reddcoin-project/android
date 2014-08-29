package com.reddcoin.android.fragments;

import java.util.ArrayList;

import com.reddcoin.android.R;
import com.reddcoin.android.activities.AuthActivity;
import com.reddcoin.android.activities.DepositsOverviewActivity;
import com.reddcoin.android.activities.MainActivity;
import com.reddcoin.android.adapters.TransactionAdapter;
import com.reddcoin.android.contentproviders.WithdrawalContentProvider;
import com.reddcoin.android.database.WithdrawalTable;
import com.reddcoin.android.model.Transaction;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class DepositsOverviewFragment extends android.support.v4.app.Fragment {
	
	private DepositsOverviewActivity ctx;
	private View rootView;
	private ListView txlv;
	private TextView empty;
	private static final int OPENEXPLORER_ID = Menu.FIRST + 1;

	public DepositsOverviewFragment () {
	}
	
	public void setContext(DepositsOverviewActivity ctx) {
		this.ctx = ctx;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_deposits, container,
				false);

		empty = (TextView) rootView.findViewById(R.id.empty);
		return rootView;
	}
	
	public void populateTransactionList(ArrayList<Transaction> txList) {
		txlv = (ListView) rootView.findViewById(R.id.txlist);

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        TransactionAdapter txAdapter = new TransactionAdapter(
                ctx, 
                android.R.layout.simple_list_item_1, txList );

        txlv.setAdapter(txAdapter);
        registerForContextMenu(txlv);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	if (v.getId() == R.id.txlist) {
	    
	    menu.add(0, OPENEXPLORER_ID, 0, R.string.menu_openexplorer);
	}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPENEXPLORER_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
		    Transaction tx = (Transaction) txlv.getItemAtPosition(info.position);

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bitinfocharts.com/reddcoin/tx/" + tx.getTxID()));
				startActivity(browserIntent);
		break;
		}
		return super.onContextItemSelected(item);
	}

	public void showIsEmpty() {
		empty.setVisibility(0);
	}
}

