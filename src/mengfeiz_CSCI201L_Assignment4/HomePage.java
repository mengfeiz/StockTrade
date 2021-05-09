package mengfeiz_CSCI201L_Assignment4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/HomePage")
public class HomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ticker = request.getParameter("ticker");
		//find info using tiingo
		String tingoInfoString = getInfoTiingo(ticker);
		String tingoPriceString = getPriceTiingo(ticker);
		String finalString = concat(tingoInfoString,tingoPriceString);
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		out.println(finalString);
		out.flush();
	} 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	public String concat(String tingoInfoString, String tingoPriceString) {
		String res = tingoInfoString.substring(0, tingoInfoString.length()-1);
		res += ", ";
		res += tingoPriceString.substring(2,tingoPriceString.length()-1);
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
		return info;
	}

}