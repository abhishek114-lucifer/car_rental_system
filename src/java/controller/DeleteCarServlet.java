package controller;

import dao.CarDAO;
import model.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/delete-car")
public class DeleteCarServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (!isAdmin(session)) {
            response.sendError(403);
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            new CarDAO().delete(id);

            session.setAttribute("flash", "Car deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/admin.jsp");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendError(500, e.getMessage());
        }
    }

    private boolean isAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }

        User user = (User) session.getAttribute("user");
        return user != null && "admin".equals(user.getRole());
    }
}
