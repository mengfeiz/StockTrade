package mengfeiz_CSCI201L_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/signUp")
public class signUp extends HttpServlet{
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
			String Email = request.getParameter("EmailNew");
			String Username = request.getParameter("UsernameNew");
			String Password = request.getParameter("PasswordNew");
			System.out.println("Username+Password is "+Username+Password);
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
			if(!userExist(Username,Password)) {
				addUser(Username, Email, Password);
				res = "Success";
			}
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.print(res);
			out.flush();
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
			System.out.println(username);
			System.out.println(numUsername);
			if(numUsername == 0)	return false;
			return true;
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
					System.out.println(queryString);
					st. executeUpdate(queryString);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

