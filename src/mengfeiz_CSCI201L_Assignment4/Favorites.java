package mengfeiz_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import javax.print.attribute.standard.JobOriginatingUserName;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;
@ServerEndpoint(value = "/fav")
public class Favorites {
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
		System.out.println("get message is "+message);
		String first ="";
		if(message.length() > 9) {
			first = message.substring(0, 9);
		}
		if(first.equals("connected")){//displayFav
			String[] values = message.split(",");
			userName = values[1];
			message = loadFav(userName);
			try {
				System.out.println(message);
				session.getBasicRemote().sendText(message);
				sessionVector.add(session);
			} catch (IOException e) {
				System.out.println("ioe: " + e.getMessage());
				close(session);
			} 
		}else {
			delFav(message);
		}
	}
	@OnClose
	public void close(Session session) {
		System.out.println("Disconnecting!");
		sessionVector.remove(session);
	}
//	public static void main(String[] args) {
//		String message = getInfoTiingo("AAPL");
//		String message2 = getPriceTiingo("AAPL");
//		System.out.println(concat(message,message2));
//	}
	
	public void delFav(String ticker){
		//connect SQL
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
			st = conn.createStatement();
			String queryString = null;
			queryString = "DELETE FROM FavoriteStock \n"
						+ "WHERE username = '"+userName+"' AND ticker ='"+ticker+"' ;";
			System.out.println(queryString);
			st. executeUpdate(queryString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String loadFav(String Username) {
		//connect SQL
		String jsonfavTString = "{";
		Set<String> favT = new HashSet<String>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
			st = conn.createStatement();
			String queryString = "SELECT ticker FROM FavoriteStock\n"
					+ "WHERE username = \""+Username+"\" Order by ticker asc;";
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				String temp = rs.getString("ticker");
				if(temp != null) {
					String message = getInfoTiingo(temp);
					String message2 = getPriceTiingo(temp);
					String res = concat(message,message2);
					jsonfavTString += "\""+temp+"\":";
					jsonfavTString += res + ",";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonfavTString = jsonfavTString.substring(0,jsonfavTString.length()-1);
		jsonfavTString += "}";
		
		return jsonfavTString;
	}
	public static String concat(String tingoInfoString, String tingoPriceString) {
		String res = tingoInfoString.substring(0, tingoInfoString.length()-1);
		res += ", ";
		res += tingoPriceString.substring(2,tingoPriceString.length()-2);
		res +="}";
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
		System.out.println(info);
		return info;
	}
}
