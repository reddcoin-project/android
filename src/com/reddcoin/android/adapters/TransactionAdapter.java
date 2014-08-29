package com.reddcoin.android.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import com.reddcoin.android.R;
import com.reddcoin.android.model.Partner;
import com.reddcoin.android.model.Transaction;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TransactionAdapter extends ArrayAdapter<Transaction>{
	
	private Activity activity;
    private ArrayList<Transaction> txList;
    private static LayoutInflater inflater = null;

	public TransactionAdapter(Activity activity, int textViewResourceId, ArrayList<Transaction> _txList) {
        super(activity, textViewResourceId, _txList);
        try {
            this.activity = activity;
            this.txList = _txList;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return txList.size();
    }

    public Transaction getItem(Transaction tx) {
        return tx;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView txID;
        public TextView txAmount;      
        public TextView txTime;
        public TextView txPartner;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.txlist, null);
                holder = new ViewHolder();

                holder.txID = (TextView) vi.findViewById(R.id.txid);
                holder.txAmount = (TextView) vi.findViewById(R.id.amount);
                holder.txTime = (TextView) vi.findViewById(R.id.timestamp);
                holder.txPartner = (TextView) vi.findViewById(R.id.address);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.txID.setText(txList.get(position).getTxID());
            holder.txAmount.setText("+" + txList.get(position).getAmount() + " RDD");
            holder.txTime.setText(txList.get(position).getTimeStamp().toString());
            Partner partner = txList.get(position).getPartners().get(0);
            holder.txPartner.setText(partner.getPartnerAddress());


        } catch (Exception e) {


        }
        return vi;
    }
}