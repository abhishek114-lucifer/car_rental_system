package controller;

import dao.CarDAO;
import model.Car;
import model.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/add-car")
public class AddCarServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (!isAdmin(session)) {
            response.sendError(403);
            return;
        }

        try {
            String imageUrl = request.getParameter("imageUrl");
            BigDecimal price = new BigDecimal(request.getParameter("pricePerDay"));

            if (!isValidImageUrl(imageUrl)) {
                throw new IllegalArgumentException("Enter a valid online HTTP(S) image URL.");
            }

            if (price.signum() <= 0) {
                throw new IllegalArgumentException("Price must be greater than zero.");
            }

            Car car = new Car();
            car.setCarName(request.getParameter("carName"));
            car.setModel(request.getParameter("model"));
            car.setPricePerDay(price);
            car.setStatus(request.getParameter("status"));
            car.setImageUrl(imageUrl.trim());

            new CarDAO().add(car);

            session.setAttribute("flash", "Car added successfully.");
            response.sendRedirect(request.getContextPath() + "/admin.jsp");

        } catch (NumberFormatException e) {
            showError(request, response, "Price must be a valid number.");
        } catch (IllegalArgumentException e) {
            showError(request, response, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showError(request, response, e.getMessage());
        }
    }

    private boolean isAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }

        User user = (User) session.getAttribute("user");
        return user != null && "admin".equals(user.getRole());
    }

    private boolean isValidImageUrl(String url) {
        return url != null
                && !url.trim().isEmpty()
                && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private void showError(HttpServletRequest request,
            HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("add-car.jsp").forward(request, response);
    }
}
