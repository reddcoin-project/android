package com.reddcoin.android.fragments;

import com.reddcoin.android.R;
import com.reddcoin.android.activities.ContactsOverviewActivity;

import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ContactsOverviewFragment extends
		android.support.v4.app.ListFragment {

	private ContactsOverviewActivity ctx;

	public ContactsOverviewFragment() {
	}

	public void setContext(ContactsOverviewActivity ctx) {
		this.ctx = ctx;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.contact_list, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setDividerHeight(2);
		registerForContextMenu(getListView());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//ctx.startDetailActivity(id);
		ctx.startMainActivity(id);
	}

	public void setListAdapter(SimpleCursorAdapter adapter) {
		setListAdapter(adapter);
	}
}
