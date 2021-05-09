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

@WebServlet("/signIn")
public class signIn extends HttpServlet{
	//web service
	private static final long serialVersionUID = 1L;
	
	//connection to client
	private BufferedReader br = null;
	private PrintWriter pw = null;
	
	// connection to database
	private static Connection conn = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String Email = request.getParameter("Email");
		String Username = request.getParameter("Username");
		String Password = request.getParameter("Password");
		String res = "exist111";
		//connect SQL
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/201_assign4?user=root&password=QAZwsxedc123");		
			st = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(Email == null) {
			if(!userExist(Username,Password)) {
				res = "don't exist";
			}
		}else {
			if(emailExist(Email)) {
				res = returnUser(Email);
			}else {
				res = "don't exist";
			}
		}
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(res);
		out.flush();
	}
	public static String returnUser(String email){
		String queryString = "SELECT username FROM 201_assign4.UserInfo\n"
				+ "WHERE  email = \""+email+"\";";
		String Username = "";
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				Username = rs.getString("Username");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Username;
	}
	public static Boolean emailExist(String email){
		String queryString = "SELECT COUNT(email) FROM 201_assign4.UserInfo\n"
				+ "WHERE  email = \""+email+"\";";
		int numUsername = -1;
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				numUsername = rs.getInt("COUNT(email)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numUsername == 0)	return false;
		return true;
	}
	public static Boolean userExist(String username,String password){
		String queryString = "SELECT COUNT(Username) FROM 201_assign4.UserInfo\n"
				+ "WHERE  Username = \""+username+"\";";
		int numUsername = -1;
		try {
			rs = st.executeQuery(queryString);
			while (rs.next()) {
				numUsername = rs.getInt("COUNT(Username)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numUsername == 0)	return false;
		String queryString2 = "SELECT Password FROM 201_assign4.UserInfo\n"
				+ "WHERE  Username = \""+username+"\";";
		String pswdString = "";
		try {
			rs = st.executeQuery(queryString2);
			while (rs.next()) {
				pswdString = rs.getString("Password");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pswdString.equals(password);
	}
	public static Boolean checkUser(String username, String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		int numUsername = -1;
		int numEmail = -1;
		try {
			rs = st.executeQuery("SELECT COUNT(username)\n"
					+ "FROM Userinfo\n"
					+ "WHERE username = '"+username+"';");
			while (rs.next()) {
				numUsername = rs.getInt("COUNT(username)");
			}
			rs = st.executeQuery("SELECT COUNT(email)\n"
					+ "FROM Userinfo\n"
					+ "WHERE username = '"+email+"';");
			while (rs.next()) {
				numEmail = rs.getInt("COUNT(email)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (email == null || !pat.matcher(email).matches()) {
			System.out.println("Invalid Email");
			return false;
		}else if(numUsername != 0) {
			System.out.println("User already exist");
			return false;
		}else if(numEmail != 0) {
			System.out.println("Email already exist");
			return false;
		}else {
			return true;
		}
	}

	public static void addUser(String username, String email, String password) {
		if(checkUser(username, email)) {
			try {
				String queryString = "INSERT INTO Userinfo (username, password, email)\n"
						+ "VALUES ('"+username+"', '"+password+"', '"+email+"');";
				st. executeUpdate(queryString);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
