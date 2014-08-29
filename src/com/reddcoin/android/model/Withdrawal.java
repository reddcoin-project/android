package com.reddcoin.android.model;

import java.sql.Timestamp;

public class Withdrawal {
	
	private int _id;
	private String address;
	private double amount;
	private String userName;
	private String owner;
	private long timestamp;
	private String txid;

	public Withdrawal(int id, String userName, String address, String owner, String txid, double amount, long timestamp) {
		this._id = id;
		this.userName = userName;
		this.address = address;
		this.owner = owner;
		this.amount = amount;
		this.timestamp = timestamp;
		this.setTxid(txid);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

}
