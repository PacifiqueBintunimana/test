package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Home
 */
@WebServlet("/ho")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().print("<h1>Hello, my name is pacifique bintunimana and my id is 25496</h1>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
        String enteredId = request.getParameter("id");
        String name = request.getParameter("name");

        if (enteredId == null || !enteredId.matches("\\d+")) {
            response.getWriter().print("<h1>ID must be a number</h1>");
            return;
        }

        Integer id = Integer.parseInt(enteredId);

        String db_url = "jdbc:postgresql://host.docker.internal:5432/best_db";
        String username = "postgres";
        String password = "";
        try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.getWriter().print("<h2>Class Not Found Error: " + e.getMessage() + "</h2>");
		}
        try (Connection con = DriverManager.getConnection(db_url, username, password)) {
            

            switch (action) {
                case "search":
                    searchStudent(con, id, response);
                    break;
                case "add":
                    if (name == null || name.isEmpty()) {
                        response.getWriter().print("<h1>Name cannot be empty</h1>");
                    } else {
                        addStudent(con, id, name, response);
                    }
                    break;
                case "delete":
                    deleteStudent(con, id, response);
                    break;
                default:
                    response.getWriter().print("<h1>Invalid action</h1>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("<h2>SQL Error: " + e.getMessage() + "</h2>");
        }/* catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().print("<h2>Class Not Found Error: " + e.getMessage() + "</h2>");
        } */catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("<h2>Unexpected Error: " + e.getMessage() + "</h2>");
        }
    }

    private void searchStudent(Connection con, Integer id, HttpServletResponse response) throws SQLException, IOException {
        String query = "SELECT * FROM student WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("names");
                    response.getWriter().print("<h1>Your name is " + escapeHtml(name) + " and your id is " + id + "</h1>");
                } else {
                    response.getWriter().print("<h2>ID doesn't exist</h2>");
                }
            }
        }
    }

    private void addStudent(Connection con, Integer id, String name, HttpServletResponse response) throws SQLException, IOException {
        String insertQuery = "INSERT INTO student (id, names) VALUES (?, ?)";
        try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
            pst.setInt(1, id);
            pst.setString(2, name);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().print("<h1>Added student with ID " + id + " and name " + escapeHtml(name) + "</h1>");
            } else {
                response.getWriter().print("<h1>Failed to add student</h1>");
            }
        }
    }

    private void deleteStudent(Connection con, Integer id, HttpServletResponse response) throws SQLException, IOException {
        String deleteQuery = "DELETE FROM student WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(deleteQuery)) {
            pst.setInt(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().print("<h1>Deleted student with ID " + id + "</h1>");
            } else {
                response.getWriter().print("<h1>Failed to delete student with ID " + id + " (ID may not exist)</h1>");
            }
        }
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
	}


