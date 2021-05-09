package mengfeiz_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.el.parser.AstConcatenation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@WebServlet("/Trade123")
public class Trade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// connection to database
	private static Connection conn = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String finalString = "";
		String Quantity = request.getParameter("Quantity");
		String action = request.getParameter("action");
		String userName = request.getParameter("userName");
		String ticker = request.getParameter("ticker");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
			st = conn.createStatement();
			String priceString = getPriceTiingo(ticker);
			if(action.equals("Buy")) {
				if(Quantity == null || Integer.parseInt(Quantity) < 1) {
					finalString = "FAILED: Purchase not possible";
				}else {
					double currentBalance = getCashBalance(userName);
					Type MapType = new TypeToken<Map<String, String>>() {}.getType();
					Map<String, String> son = new Gson().fromJson(priceString,MapType);
					double currentPrice = Double.parseDouble(son.get("askPrice"));
					double amount = currentPrice*Integer.parseInt(Quantity);
					if(currentBalance < amount) {
						finalString = "FAILED: Purchase not possible";
					}else {
						//create purchase
						String queryString = "INSERT INTO SaleRec (Username,TickerName,Amount,Price,isPurchase)\n"
								+ "VALUES (\""+userName+"\",\""+ticker+"\","+Quantity+","+currentPrice+",1)";
						System.out.println(queryString);
						st. executeUpdate(queryString);
						//delete from balance
						queryString = "UPDATE UserInfo\n"
								+ "SET Balance = (Balance-"+amount+")\n"
								+ "WHERE Username = \""+userName+"\";";
						st. executeUpdate(queryString);
						finalString = "SUCCESS: Executed purchase of "+Quantity+" shares of "+ticker+" for $"+currentPrice;
					}
				}
			}else {//sell
				System.out.println("Quantity is "+Quantity);
				if(Quantity == null || Integer.parseInt(Quantity) < 1 || Integer.parseInt(Quantity) > getHoldStock(userName,ticker)) {
					finalString = "FAILED: Sale not possible";
				}else {
					double currentBalance = getCashBalance(userName);
					Type MapType = new TypeToken<Map<String, String>>() {}.getType();
					Map<String, String> son = new Gson().fromJson(priceString,MapType);
					double currentPrice = Double.parseDouble(son.get("bidPrice"));
					double amount = currentPrice*Integer.parseInt(Quantity);
					//create sale
					String queryString = "INSERT INTO SaleRec (Username,TickerName,Amount,Price,isPurchase)\n"
							+ "VALUES (\""+userName+"\",\""+ticker+"\","+Quantity+","+currentPrice+",0)";
					System.out.println(queryString);
					st. executeUpdate(queryString);
					//add to balance
					queryString = "UPDATE UserInfo\n"
							+ "SET Balance = (Balance+"+amount+")\n"
							+ "WHERE Username = \""+userName+"\";";
					st. executeUpdate(queryString);
					finalString = "SUCCESS: Executed sale of "+Quantity+" shares of "+ticker+" for $"+currentPrice;
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setContentType("text/html;charset=UTF-8");
		System.out.println("finalString is "+finalString);
		PrintWriter out = response.getWriter();
		out.print(finalString);
		out.flush();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	public static double getHoldStock(String userName,String ticker) {
		double buy = 0;
		double sale = 0;
		String queryString = "SELECT SUM(Amount) FROM 201_assign4.SaleRec\n"
				+ "WHERE username = \""+userName+"\" AND TickerName=\""+ticker+"\" AND isPurchase =1;";
		String querySaleString = "SELECT SUM(Amount) FROM 201_assign4.SaleRec\n"
				+ "WHERE username = \""+userName+"\" AND TickerName=\""+ticker+"\" AND isPurchase =0;";
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				buy = rs.getDouble("SUM(Amount)");
			}
			rs = st.executeQuery(querySaleString);
			while (rs.next()) {
				sale = rs.getDouble("SUM(Amount)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buy-sale;
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
		price = price.substring(1,price.length()-1);
		return price;
//		Type MapType = new TypeToken<Map<String, String>>() {}.getType();
//		Map<String, String> son = new Gson().fromJson(price,MapType);
//		double currentPrice = Double.parseDouble(son.get("askPrice"));
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
}
