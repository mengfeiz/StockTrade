package mengfeiz_CSCI201L_Assignment4;

public class profileTickerCard {
	private String ticker;
	private String name;
	private double quantity;
	private double avgCost;
	private double totalCost;
	
	private double change;
	private double currentPrice;
	private double marketValue;
	
	public profileTickerCard(String ticker,String name,double quantity,double avgCost,double totalCost,double change,double currentPrice,double marketValue) {
		this.ticker = ticker;
		this.name = name;
		this.quantity = quantity;
		this.avgCost = avgCost;
		this.totalCost = totalCost;
		this.change = change;
		this.currentPrice = currentPrice;
		this.marketValue = marketValue;
	}
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getAvgCost() {
		return avgCost;
	}
	public void setAvgCost(double avgCost) {
		this.avgCost = avgCost;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getChange() {
		return change;
	}
	public void setChange(double change) {
		this.change = change;
	}
	public double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	public double getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}
}
