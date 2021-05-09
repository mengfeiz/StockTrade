package mengfeiz_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/changeFav")

public class changeFav extends HttpServlet{
	//web service
	private static final long serialVersionUID = 1L;
	
	// connection to database
	private static Connection conn = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String input = request.getParameter("myinput");
		System.out.println(input);
		String userName = request.getParameter("userName");
		System.out.println("userName is "+userName);
		String[] parts = input.split(",");
		String action = parts[0];
		String ticker = parts[1];
//		String username = parts[2];
		//connect SQL
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
			st = conn.createStatement();
			String queryString = null;
			if(action.equals("addFav")) {
				queryString = "INSERT INTO FavoriteStock (username,ticker)\n"
						+ "VALUES ('"+userName+"','"+ticker+"');";
			}else {
				queryString = "DELETE FROM FavoriteStock \n"
						+ "WHERE username = '"+userName+"' AND ticker ='"+ticker+"' ;";
			}
			System.out.println(queryString);
			st. executeUpdate(queryString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.print("change success");
	}

}
