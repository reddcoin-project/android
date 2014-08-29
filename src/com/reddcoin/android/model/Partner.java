package com.reddcoin.android.model;

import java.math.BigDecimal;

public class Partner {
	
	private String partnerAddress;
	private BigDecimal amount;

	public Partner(String partnerAddress, BigDecimal amount) {
		this.setPartnerAddress(partnerAddress);
		this.setAmount(amount);
	}

	public String getPartnerAddress() {
		return partnerAddress;
	}

	public void setPartnerAddress(String partnerAddress) {
		this.partnerAddress = partnerAddress;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
