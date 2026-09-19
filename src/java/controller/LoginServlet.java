package controller;

import dao.UserDAO;
import model.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null
                || email.trim().isEmpty() || password.isEmpty()) {
            showError(request, response, "Email and password are required.");
            return;
        }

        try {
            User user = new UserDAO().login(email.trim(), password);

            if (user == null) {
                showError(request, response, "Invalid email or password.");
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            if ("admin".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError(request, response, e.getMessage());
        }
    }

    private void showError(HttpServletRequest request,
            HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
