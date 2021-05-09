package mengfeiz_CSCI201L_Assignment4;

import java.util.Vector;

public class profileClass {
	private double cashBalance;
	private double totalAccountValue;
	
	private Vector<profileTickerCard> profileTickers = new Vector<profileTickerCard>();

	public double getCashBalance() {
		return cashBalance;
	}

	public void setCashBalance(double cashBalance) {
		this.cashBalance = cashBalance;
	}

	public double getTotalAccountValue() {
		return totalAccountValue;
	}

	public void setTotalAccountValue(double totalAccountValue) {
		this.totalAccountValue = totalAccountValue;
	}

	public Vector<profileTickerCard> getProfileTickers() {
		return profileTickers;
	}

	public void setProfileTickers(Vector<profileTickerCard> profileTickers) {
		this.profileTickers = profileTickers;
	}
	
	public void addProfileTickers(profileTickerCard tickerCard) {
		this.profileTickers.add(tickerCard);
	}
}
