package mengfeiz_CSCI201L_Assignment4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

@WebServlet("/afterLogHome")
public class afterLogHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// connection to database
	private static Connection conn = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ticker = request.getParameter("ticker");
		String userName = request.getParameter("userName");
		System.out.println("userName is "+userName);
		//find info using tiingo
		String tingoInfoString = getInfoTiingo(ticker);
		String tingoPriceString = getPriceTiingo(ticker);
		Boolean isFavBoolean = isFav(ticker,userName);
		String finalString = concat(tingoInfoString,tingoPriceString,isFavBoolean);
		response.setContentType("text/html;charset=UTF-8");
		System.out.println(finalString);
		PrintWriter out = response.getWriter();
		out.println(finalString);
		out.flush();
	}
//	public static void main(String[] args) {
//		String tingoInfoString = getInfoTiingo("AAPL");
//		String tingoPriceString = getPriceTiingo("AAPL");
//		Boolean isFavBoolean = isFav("AAPL","Fiona");
//		String finalString = concat(tingoInfoString,tingoPriceString,isFavBoolean);
//		System.out.println(finalString);
//	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	public static String concat(String tingoInfoString, String tingoPriceString,Boolean isFavBoolean) {
		String res = tingoInfoString.substring(0, tingoInfoString.length()-1);
		res += ", ";
		res += tingoPriceString.substring(2,tingoPriceString.length()-2);
		if(isFavBoolean){
			res += ", \"isFav\": \"isFav\"";
		}else {
			res += ", \"isFav\": \"notFav\"";
		}
		res +="}";
		return res;
	}
	public Boolean isFav(String ticker,String Username) {
		//connect SQL
		int temp = -1;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
			st = conn.createStatement();
			String queryString = "SELECT COUNT(ticker) FROM FavoriteStock\n"
					+ "WHERE username = \""+Username+"\" AND ticker = \""+ticker+"\";";
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				temp = rs.getInt("COUNT(ticker)");
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp > 0;
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
		return info;
	}

}