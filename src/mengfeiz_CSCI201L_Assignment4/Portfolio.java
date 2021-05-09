package mengfeiz_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@ServerEndpoint(value = "/Portfolio")
public class Portfolio {
	private String userName = "userName";
	// connection to database
	private static Connection conn = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
	private static Vector<Session> sessionVector = new Vector<Session>();
	
	@OnOpen
	public void open(Session session) {
		sessionVector.add(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			String jsonMessage ="";
			profileClass myProfile = new profileClass();
			//connect SQL
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
				st = conn.createStatement();
				Vector<String> tickerName = getHoldTickers(message);
				double totalAccountValue = 0;
				for(String tickerString : tickerName) {
					double totalCost = getTickerTotalCost(tickerString,message);
					double tickerQuantity = getTickerQuantity(tickerString,message);
					double avgCost = 0;
					if(tickerQuantity != 0)
						avgCost = totalCost/tickerQuantity;
					String jsonPriceString = getPriceTiingo(tickerString);
					String jsonInfoString = getInfoTiingo(tickerString);
					jsonPriceString = jsonPriceString.substring(1,jsonPriceString.length()-1);
					Type MapType = new TypeToken<Map<String, String>>() {}.getType();
					Map<String, String> son = new Gson().fromJson(jsonPriceString,MapType);
					Map<String, String> sonInfo = new Gson().fromJson(jsonInfoString,MapType);
					double currentPrice = Double.parseDouble(son.get("last"));
					String name = sonInfo.get("name");
					double change = avgCost-currentPrice;
					double marketValue = currentPrice * tickerQuantity;
					totalAccountValue += marketValue;
					System.out.println("tickerQuantity is "+tickerQuantity);
					profileTickerCard tickerCard = new profileTickerCard(tickerString,name,tickerQuantity,avgCost,totalCost,change,currentPrice,marketValue);
					myProfile.addProfileTickers(tickerCard);
				}
				double cashBalance = getCashBalance(message);
				totalAccountValue += cashBalance;
				myProfile.setCashBalance(cashBalance);
				myProfile.setTotalAccountValue(totalAccountValue);
				Gson gson = new Gson();
				jsonMessage = gson.toJson(myProfile);
				System.out.println(jsonMessage);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.getBasicRemote().sendText(jsonMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@OnClose
	public void close(Session session) {
		System.out.println("Disconnecting!");
		sessionVector.remove(session);
	}
	public static double getCashBalance(String userName) {
		double res = 0;
		String queryString = "SELECT Balance FROM 201_assign4.UserInfo\n"
				+ "where username = \""+userName+"\";";
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				res = rs.getDouble("Balance");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public static String getPriceTiingo(String ticker) {
		String priceString ="https://api.tiingo.com/iex?tickers="+ ticker+ "&token=0190f53e267e595d1a5be925d5ad0e1bcba69f09";
		String price = null;
		try {
			 URL url1 = new URL(priceString);
            HttpURLConnection httpCon1 = (HttpURLConnection) url1.openConnection();
			httpCon1.setRequestMethod("GET");
			InputStreamReader inputJson1 = new InputStreamReader(httpCon1.getInputStream());
            BufferedReader br1 = new BufferedReader(inputJson1);
            //read in br
            price = br1.readLine();
		}catch (Exception e) {
			// TODO: handle exception
		}
		//concat info and price to res
		return price;
	}
	public static double getTickerQuantity(String tickerString,String message) {
		double res = 0;
		String queryString = "SELECT SUM(Amount) FROM 201_assign4.SaleRec\n"
				+ "WHERE username = \""+message+"\" AND TickerName=\""+tickerString+"\"AND isPurchase =1;";
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				res = rs.getDouble("SUM(Amount)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		queryString = "SELECT SUM(Amount) FROM 201_assign4.SaleRec\n"
				+ "WHERE username = \""+message+"\" AND TickerName=\""+tickerString+"\"AND isPurchase =0;";
		double minius = 0;
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				minius = rs.getDouble("SUM(Amount)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res-minius;
	}
	public static double getTickerTotalCost(String tickerString,String message) {
		double res = 0;
		String queryString = "SELECT SUM(Amount*Price) FROM 201_assign4.SaleRec\n"
				+ "WHERE username = \""+message+"\" AND TickerName=\""+tickerString+"\"";
		
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				res = rs.getDouble("SUM(Amount*Price)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public static Vector<String> getHoldTickers(String userName) {
		Vector<String> tickerName = new Vector<String>();
		String queryString = "SELECT TickerName FROM 201_assign4.SaleRec\n"
				+ "WHERE username = \""+userName+"\"AND isPurchase =1 \n"
				+ "GROUP BY TickerName Order by TickerName asc;";
		String ticker = "";
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				ticker = rs.getString("TickerName");
				tickerName.add(ticker);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tickerName;
	}
	public static String getInfoTiingo(String ticker) {
//		using Tiingo API
		String urlString = "https://api.tiingo.com/tiingo/daily/"+ ticker+ "?token=0190f53e267e595d1a5be925d5ad0e1bcba69f09";
		String info = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			InputStreamReader inputJson = new InputStreamReader(httpCon.getInputStream());
            BufferedReader br = new BufferedReader(inputJson);
            //read in br
            info = br.readLine();
		} catch (Exception e) {
			// TODO: handle exception
		}
		//concat info and price to res
		return info;
	}
}
