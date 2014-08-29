package com.reddcoin.android.fragments;

import com.reddcoin.android.R;
import com.reddcoin.android.activities.MainActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PurseFragment extends android.support.v4.app.Fragment {

	private EditText grossRDD;
	private EditText feeRDD;
	private EditText netRDD;
	private EditText addressField;

	private TextView addressValue;
	private TextView balanceValue;

	private Button qrButton;
	private Button sendButton;

	private View rootView;

	private MainActivity ctx;

	public PurseFragment() {
	}

	public void setContext(MainActivity ctx) {
		this.ctx = ctx;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_main, container, false);

		grossRDD = (EditText) rootView.findViewById(R.id.grossRDD);
		feeRDD = (EditText) rootView.findViewById(R.id.feeRDD);
		netRDD = (EditText) rootView.findViewById(R.id.netRDD);
		addressField = (EditText) rootView.findViewById(R.id.receiver);

		addressValue = (TextView) rootView.findViewById(R.id.address_value);
		balanceValue = (TextView) rootView.findViewById(R.id.balance_value);
		balanceValue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String visibleValue = balanceValue.getText().toString();
				if (!visibleValue.equals("--")) {
					grossRDD.requestFocus();
					grossRDD.setText(visibleValue);
				}
			}
		});
		qrButton = (Button) rootView.findViewById(R.id.qr);
		qrButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				qrButton.setEnabled(false);
				try {
					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
					ctx.startActivityForResult(intent, 0);
				} catch (ActivityNotFoundException e) {
					ctx.showErrorMessage("zXing Barcode Scanner not found!");
				}
			}
		});
		sendButton = (Button) rootView.findViewById(R.id.send_button);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!netRDD.getText().toString().equals("")
						&& !addressField.getText().toString().equals("")) {
					ctx.startTransaction(addressField.getText().toString(),
							Double.parseDouble(grossRDD.getText().toString()));
				}
			}
		});

		addressValue.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				String text = addressValue.getText().toString().trim();
				if (text.length() > 0) {
					ctx.showMessage("Address copied to clipboard");
					if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
						android.text.ClipboardManager clipboardMgr = (android.text.ClipboardManager) ctx
								.getSystemService(Context.CLIPBOARD_SERVICE);
						clipboardMgr.setText(text);
					} else {
						android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) ctx
								.getSystemService(Context.CLIPBOARD_SERVICE);
						ClipData clip = ClipData.newPlainText("Copied text",
								text);
						clipboardMgr.setPrimaryClip(clip);
					}
				}
			}
		});
		createLayout(rootView);
		return rootView;
	}

	public void createLayout(View view) {

		feeRDD.setKeyListener(null);
		feeRDD.setFocusable(false);
		feeRDD.setFocusableInTouchMode(false);

		grossRDD.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				if (grossRDD.isFocused()) {
					if(grossRDD.getText().toString().equals(".")) {
						grossRDD.setError("Invalid amount");
						netRDD.setText("");
						feeRDD.setText("");
						return;
					}
					if (!grossRDD.getText().toString().equals("")) {
						feeRDD.setText(""
								+ new BigDecimal(grossRDD.getText().toString())
										.divide(new BigDecimal("200"), 8,
												RoundingMode.HALF_UP));
						netRDD.setText(""
								+ new BigDecimal(grossRDD.getText().toString())
										.subtract(new BigDecimal(feeRDD
												.getText().toString())));
						if (netRDD.getText().toString().contains("E")
								|| feeRDD.getText().toString().contains("E")) {
							grossRDD.setError("Invalid amount");
							netRDD.setText("");
							feeRDD.setText("");
						}
					}
					else if (grossRDD.getText().toString().equals("")){
						netRDD.setText("");
						feeRDD.setText("");
					}
				} 
				else {
					if (!grossRDD.getText().toString().equals("")) {
						feeRDD.setText(""
								+ new BigDecimal(grossRDD.getText().toString())
										.divide(new BigDecimal("200"), 8,
												RoundingMode.HALF_UP));
					}
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		netRDD.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				if (netRDD.isFocused()) {
					if(netRDD.getText().toString().equals(".")) {
						netRDD.setError("Invalid amount");
						grossRDD.setText("");
						feeRDD.setText("");
						return;
					}
					if (!netRDD.getText().toString().equals("")) {
						grossRDD.setText(""
								+ new BigDecimal(netRDD.getText().toString())
										.divide(new BigDecimal("0.995"), 8,
												RoundingMode.HALF_UP));
						if (grossRDD.getText().toString().contains("E")
								|| feeRDD.getText().toString().contains("E")) {
							netRDD.setError("Invalid amount");
							grossRDD.setText("");
							feeRDD.setText("");
						}
					} else {
						grossRDD.setText("");
						feeRDD.setText("");
					}
				} else {
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	public void setAddressValue(String address) {
		if(addressValue != null && address != null)
		addressValue.setText("" + address);
	}

	public void setAddressFieldValue(String address) {
		addressField.setText("" + address);
	}

	public void setBalanceValue(double balance) {
		balanceValue.setText("" + balance);
	}

	public View getQRImageView() {
		return rootView.findViewById(R.id.qr_image);
	}

	public void enableQRButton() {
		qrButton.setEnabled(true);
	}
}
