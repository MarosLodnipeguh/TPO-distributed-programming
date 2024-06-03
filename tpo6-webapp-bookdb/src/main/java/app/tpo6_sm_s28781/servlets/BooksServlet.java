package app.tpo6_sm_s28781.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "BooksServlet", urlPatterns = "/books")
public class BooksServlet extends HttpServlet {

    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void serviceRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:derby:ksidb;create=true");

            Statement statement = connection.createStatement();
            ResultSet resultSet;

            String searchTerm = req.getParameter("search");

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String query = "SELECT * FROM POZYCJE WHERE TYTUL LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchTerm + "%");
                resultSet = preparedStatement.executeQuery();
            } else {
                resultSet = statement.executeQuery("SELECT * FROM POZYCJE");
            }

            resp.setContentType("text/html; charset=windows-1250");
            PrintWriter out = resp.getWriter();

            out.println("<h2>Wyszukiwarka książek</h2>");
            out.println("<form method='get' action='books'>");
            out.println("<input type='text' name='search' placeholder='Wyszukaj książkę...'>");
            out.println("<input type='submit' value='Szukaj'>");
            out.println("</form>");

            out.println("<h2>Lista dostępnych książek</h2>");
            out.println("<p>ISBN - AUTID - TYTUL - WYDID - ROK - CENA</p>");

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                int autid = resultSet.getInt("autid");
                String tytul = resultSet.getString("tytul");
                int wydid = resultSet.getInt("wydid");
                int rok = resultSet.getInt("rok");
                float cena = resultSet.getFloat("cena");
                out.println("<p>" + isbn + " - " + autid + " - " + tytul + " - " + wydid + " - " + rok + " - " + cena + "</p>");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        serviceRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        serviceRequest(request, response);
    }
}
