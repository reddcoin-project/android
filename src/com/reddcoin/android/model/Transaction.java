package com.reddcoin.android.model;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Transaction {

	private String txID;
	private int block;
	private Date timestamp;
	private boolean isCredit;
	private BigDecimal amount;
	private BigDecimal newBalance;
	private ArrayList<Partner> partners;

	public Transaction(String txID, int block, Date timestamp, boolean isCredit, BigDecimal amount, BigDecimal newBalance, ArrayList<Partner> partners) {
		this.txID = txID;
		this.block = block;
		this.timestamp = timestamp;
		this.isCredit = isCredit;
		this.amount = amount;
		this.newBalance = newBalance;
		this.partners = partners;
	}

	public String getTxID() {
		return txID;
	}

	public void setTxID(String txID) {
		this.txID = txID;
	}

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public boolean getIsCredit() {
		return isCredit;
	}
	
	public void setTimeStamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Date getTimeStamp() {
		return timestamp;
	}

	public void setType(boolean isCredit) {
		this.isCredit = isCredit;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getNewBalance() {
		return newBalance;
	}

	public void setNewBalance(BigDecimal newBalance) {
		this.newBalance = newBalance;
	}

	public ArrayList<Partner> getPartners() {
		return partners;
	}

	public void setPartners(ArrayList<Partner> partners) {
		this.partners = partners;
	}

	
}
