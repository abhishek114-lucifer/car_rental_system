package controller;

import dao.UserDAO;
import model.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (name == null || email == null || password == null
                || name.trim().isEmpty()
                || email.trim().isEmpty()
                || password.isEmpty()) {
            showError(request, response, "All fields are required.");
            return;
        }

        email = email.trim();

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            showError(request, response, "Enter a valid email address.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();

            if (dao.emailExists(email)) {
                showError(request, response, "Email already registered.");
                return;
            }

            User user = new User();
            user.setName(name.trim());
            user.setEmail(email);
            user.setPassword(password);

            dao.register(user);

            HttpSession session = request.getSession();
            session.setAttribute("flash", "Registration successful. Please log in.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            showError(request, response, "Database error: " + e.getMessage());
        }
    }

    private void showError(HttpServletRequest request,
            HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}
