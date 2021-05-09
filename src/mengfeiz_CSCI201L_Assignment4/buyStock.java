package mengfeiz_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/buyStock")

public class buyStock extends HttpServlet{
	//web service
	private static final long serialVersionUID = 1L;
	
	// connection to database
	private static Connection conn = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ticker = request.getParameter("ticker");
		String userName = request.getParameter("userName");
		String price = request.getParameter("Price");
		String Quantity = request.getParameter("Quantity");
		double amount = Double.parseDouble(price) * Double.parseDouble(Quantity);
		System.out.println("ticker is "+ticker);
		System.out.println("userName is "+userName);
		System.out.println("Quantity is "+Quantity);
		String outputMessage = "Default Message";
		if(Quantity == null || Integer.parseInt(Quantity)<1) {
			outputMessage = "FAILED: Purchase not possible";
		}else if(checkBalance(amount,userName)) {
			//connect SQL
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
				st = conn.createStatement();
				//create purchase
				String queryString = "INSERT INTO SaleRec (Username,TickerName,Amount,Price,isPurchase)\n"
						+ "VALUES (\""+userName+"\",\""+ticker+"\","+Quantity+","+price+",1)";
				System.out.println(queryString);
				st. executeUpdate(queryString);
				//delete from balance
				queryString = "UPDATE UserInfo\n"
						+ "SET Balance = (Balance-"+amount+")\n"
						+ "WHERE Username = \""+userName+"\";";
				st. executeUpdate(queryString);
				outputMessage = "SUCCESS: Executed purchase of "+Quantity+" shares of "+ticker+" for $"+price;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			outputMessage = "FAILED: Purchase not possible";
		}
		PrintWriter out = response.getWriter();
		System.out.println(outputMessage);
		out.print(outputMessage);
		out.flush();
	}
	
	public Boolean checkBalance(double amount,String userName) {
		double balance = 0;
		//connect SQL
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
			st = conn.createStatement();
			String queryString = "SELECT Balance FROM 201_assign4.UserInfo\n"
					+ "WHERE userName = \""+userName+"\";";
			System.out.println(queryString);
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				balance = rs.getDouble("Balance");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return balance >= amount;
	}
}
