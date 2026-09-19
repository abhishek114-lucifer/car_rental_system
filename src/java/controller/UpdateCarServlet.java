package controller;

import dao.CarDAO;
import model.Car;
import model.User;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/update-car")
public class UpdateCarServlet extends HttpServlet {

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
            car.setId(Integer.parseInt(request.getParameter("id")));
            car.setCarName(request.getParameter("carName"));
            car.setModel(request.getParameter("model"));
            car.setPricePerDay(price);
            car.setStatus(request.getParameter("status"));
            car.setImageUrl(imageUrl.trim());

            new CarDAO().update(car);

            session.setAttribute("flash", "Car updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("update-car.jsp").forward(request, response);
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
}
