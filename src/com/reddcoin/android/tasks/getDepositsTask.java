package com.reddcoin.android.tasks;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.reddcoin.android.activities.DepositsOverviewActivity;
import com.reddcoin.android.dialogs.CustomProgressDialog;
import com.reddcoin.android.model.Partner;
import com.reddcoin.android.model.Transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class getDepositsTask extends
		AsyncTask<String, Integer, ArrayList<Transaction>> {

	private DepositsOverviewActivity ctx;
	private ArrayList<Transaction> txList;
	private CustomProgressDialog dialog;
	private boolean sourceIsAccessible = true;

	public getDepositsTask(DepositsOverviewActivity ctx) {
		this.ctx = ctx;
	}

	protected void onPreExecute() {
		dialog = new CustomProgressDialog(ctx);
		dialog.setMessage("Downloading deposit-info..");
		dialog.show();
	}

	@SuppressLint("SimpleDateFormat")
	protected ArrayList<Transaction> doInBackground(String... address) {
		txList = new ArrayList<Transaction>();
		if (address != null && isOnline()) {
			Document doc = null;
			try {
				doc = Jsoup.connect(
						"http://bitinfocharts.com/reddcoin/address/"
								+ address[0]).get();
			} catch (IOException e) {
				sourceIsAccessible = false;
			}
			if (sourceIsAccessible) {
			for (Element table : doc.select("#table_maina")) {
				for (Element row : table.select("tr")) {
					Elements tds = row.select("td");
					if (tds.size() > 0) {
						boolean isCredit;
						if (tds.get(3).text().contains("+")) {
							isCredit = true;
						} else {
							isCredit = false;
						}
						ArrayList<Partner> partners = new ArrayList<Partner>();
						boolean isDeposit = false;
						for (Element partnerAddress : tds.get(0).select("a")) {
							if (partnerAddress.text().contains("R")) {

								String txAction = partnerAddress
										.previousSibling().toString();
								
								if (txAction.contains("from")) {
									String customTxAction = partnerAddress
											.nextSibling().toString();
									String txActionWithoutArrow = customTxAction.substring(3, customTxAction.length());
									partners.add(new Partner(partnerAddress
											.text().replace(" RDD", ""),
											new BigDecimal(txActionWithoutArrow
													.split("RDD")[0].replace(
													" ", "")
													.replace(",", ""))));
									isDeposit = true;
								}
							}
						}
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						simpleDateFormat.setTimeZone(TimeZone
								.getTimeZone("UTC"));
						Date myDate = null;
						try {
							myDate = simpleDateFormat.parse(tds.get(2).text()
									.replace(" UTC", ""));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if(isDeposit) {
						txList.add(new Transaction(tds.get(0)
								.select("a[href*=../tx]").text(), Integer
								.parseInt(tds.get(1).text()), myDate, isCredit,
								new BigDecimal(tds.get(3).text()
										.replace(" RDD", "").replace("+", "")
										.replace("-", "").replace(",", "")),
								new BigDecimal(tds.get(4).text()
										.replace(" RDD", "").replace(",", "")),
								partners));
						}
					}
				}
			}
			}
		} 
		else {
			
		}
		Collections.reverse(txList);
		return txList;
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(ArrayList<Transaction> result) {
		if (!sourceIsAccessible) {
			ctx.showErrorMessage("Cannot access our source: bitinfocharts.com!");
			ctx.showIsEmptyMessage();
		}
		else {
		if(result != null) {
		if (!result.isEmpty()) {
			ctx.transactionsCallback(result);
		} else {
			if(!isOnline()) 
		    ctx.showErrorMessage("No connection available!");	
			else
			ctx.showErrorMessage("No deposits found!");
			ctx.showIsEmptyMessage();
		}
		}
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
