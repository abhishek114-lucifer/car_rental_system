package controller;

import dao.BookingDAO;
import model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/return-car")
public class ReturnCarServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null || !"customer".equals(user.getRole())) {
            response.sendError(403);
            return;
        }

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            new BookingDAO().returnBooking(bookingId, user.getId());

            session.setAttribute("flash", "Car returned successfully.");
            response.sendRedirect(request.getContextPath() + "/bookings.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, e.getMessage());
        }
    }
}
