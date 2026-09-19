package controller;

import dao.BookingDAO;
import dao.CarDAO;
import model.Booking;
import model.Car;
import model.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/book-car")
public class BookCarServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null || !"customer".equals(user.getRole())) {
            response.sendError(403);
            return;
        }

        try {
            int carId = Integer.parseInt(request.getParameter("carId"));
            Date rentDate = Date.valueOf(request.getParameter("rentDate"));
            Date returnDate = Date.valueOf(request.getParameter("returnDate"));

            long days = ChronoUnit.DAYS.between(
                    rentDate.toLocalDate(), returnDate.toLocalDate());

            if (days < 1) {
                throw new IllegalArgumentException("Return date must be after the rent date.");
            }

            Car car = new CarDAO().findById(carId);

            if (car == null || !"Available".equals(car.getStatus())) {
                throw new IllegalArgumentException("This car is not available.");
            }

            Booking booking = new Booking();
            booking.setUserId(user.getId());
            booking.setCarId(carId);
            booking.setRentDate(rentDate);
            booking.setReturnDate(returnDate);

            BigDecimal total = car.getPricePerDay()
                    .multiply(BigDecimal.valueOf(days));
            booking.setTotalAmount(total);

            new BookingDAO().create(booking);

            session.setAttribute("flash", "Car booked successfully.");
            response.sendRedirect(request.getContextPath() + "/bookings.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("book.jsp").forward(request, response);
        }
    }
}
