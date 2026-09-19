<%@ page contentType="text/html;charset=UTF-8" import="dao.CarDAO,model.Car,model.User,util.CurrencyFormatter,util.Html,java.util.List" %>

<%
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<Car> cars = null;
    String error = null;

    try {
        cars = new CarDAO().findAvailable();
    } catch (Exception e) {
        error = e.getMessage();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Available Cars | DriveEasy</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

<nav>
    <a class="brand"
       href="${pageContext.request.contextPath}/dashboard.jsp">
        DRIVEEASY
    </a>

    <div>
        <a href="${pageContext.request.contextPath}/bookings.jsp">
            My bookings
        </a>

        <a href="${pageContext.request.contextPath}/logout">
            Log out
        </a>
    </div>
</nav>

<main class="page">

    <div class="page-heading">

        <div>
            <p class="eyebrow">THE FLEET</p>
            <h1>Available cars</h1>
        </div>

        <a class="button small"
           href="${pageContext.request.contextPath}/book.jsp">
            Book a car <span>-></span>
        </a>

    </div>


    <% if (error != null) { %>

        <div class="message error">
            <%= Html.escape(error) %>
        </div>

    <% } else if (cars == null || cars.isEmpty()) { %>

        <div class="empty">
            No cars are available right now.
        </div>

    <% } else { %>

        <div class="car-grid">

            <% for (Car car : cars) { %>

                <article class="car-card">

                    <%
                        String imageUrl = car.getImageUrl();

                        if (imageUrl == null ||
                            imageUrl.trim().isEmpty()) {

                            imageUrl =
                                "https://placehold.co/1200x750/e8ebe5/18211f?text=Car";
                        }
                    %>

                    <img class="car-image"
                         src="<%= Html.escape(imageUrl) %>"
                         alt="<%= Html.escape(car.getCarName()) %>"
                         onerror="this.onerror=null;this.src='https://placehold.co/1200x750/e8ebe5/18211f?text=Car';">

                    <div class="car-number">
                        CAR 0<%= car.getId() %>
                    </div>

                    <h2>
                        <%= Html.escape(car.getCarName()) %>
                    </h2>

                    <p>
                        <%= Html.escape(car.getModel()) %>
                    </p>

                    <div class="car-meta">

                        <strong>
                            <%= CurrencyFormatter.inr(car.getPricePerDay()) %>
                        </strong>

                        <span>/ day</span>

                    </div>

                    <span class="status available">
                        <%= Html.escape(car.getStatus()) %>
                    </span>

                    <a class="text-link"
                       href="${pageContext.request.contextPath}/book.jsp?carId=<%= car.getId() %>">
                        Book now ->
                    </a>

                </article>

            <% } %>

        </div>

    <% } %>

</main>

</body>
</html>