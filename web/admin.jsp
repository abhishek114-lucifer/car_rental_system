<%@ page contentType="text/html;charset=UTF-8"
	import="dao.CarDAO,dao.BookingDAO,dao.UserDAO,model.Car,model.Booking,model.User,util.CurrencyFormatter,util.Html,java.util.List" %>
<%
	User user = (User) session.getAttribute("user");
	if (user == null || !"admin".equals(user.getRole())) {
		response.sendError(403);
		return;
	}

	List<Car> cars = null;
	List<Booking> bookings = null;
	List<User> users = null;
	String error = null;
	String flash = (String) session.getAttribute("flash");
	session.removeAttribute("flash");
	try {
		cars = new CarDAO().findAll();
		bookings = new BookingDAO().findAll();
		users = new UserDAO().findAll();
	} catch (Exception exception) {
		error = exception.getMessage();
	}
%>
<!DOCTYPE html>
<html>
<head>
	<title>Admin | DriveEasy</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<nav>
		<a class="brand" href="${pageContext.request.contextPath}/admin.jsp">DRIVEEASY / ADMIN</a>
		<div>
			<span class="nav-user"><%=Html.escape(user.getName())%></span>
			<a href="${pageContext.request.contextPath}/logout">Log out</a>
		</div>
	</nav>

	<main class="page">
		<div class="page-heading">
			<div>
				<p class="eyebrow">CONTROL CENTRE</p>
				<h1>Fleet overview</h1>
			</div>
			<a class="button small" href="${pageContext.request.contextPath}/add-car.jsp">Add car <span>+</span></a>
		</div>

		<% if (flash != null) { %>
			<div class="message success"><%=Html.escape(flash)%></div>
		<% } %>
		<% if (error != null) { %>
			<div class="message error"><%=Html.escape(error)%></div>
		<% } %>

		<div class="admin-section">
			<h2>Fleet <span class="muted">(<%=cars == null ? 0 : cars.size()%>)</span></h2>
			<div class="car-grid">
				<% if (cars != null) {
					for (Car car : cars) {
				%>
					<article class="car-card">
						<img class="car-image"
							 src="<%=Html.escape(car.getImageUrl() == null || car.getImageUrl().trim().isEmpty() ? "https://placehold.co/1200x750/e8ebe5/18211f?text=Car" : car.getImageUrl())%>"
							 alt="<%=Html.escape(car.getCarName())%>"
							 onerror="this.onerror=null;this.src='https://placehold.co/1200x750/e8ebe5/18211f?text=Car';">
						<div class="car-number">CAR 0<%=car.getId()%></div>
						<h2><%=Html.escape(car.getCarName())%></h2>
						<p><%=Html.escape(car.getModel())%></p>
						<div class="car-meta">
							<strong><%=CurrencyFormatter.inr(car.getPricePerDay())%></strong>
							<span>/ day</span>
						</div>
						<span class="status"><%=Html.escape(car.getStatus())%></span>
						<div class="admin-actions">
							<a href="${pageContext.request.contextPath}/update-car.jsp?id=<%=car.getId()%>">Edit</a>
							<form method="post" action="${pageContext.request.contextPath}/delete-car">
								<input type="hidden" name="id" value="<%=car.getId()%>">
								<button class="link-button danger" type="submit">Delete</button>
							</form>
						</div>
					</article>
				<%
					}
				}
				%>
			</div>
		</div>

		<div class="admin-section">
			<h2>All bookings</h2>
			<div class="table-wrap">
				<table>
					<thead>
						<tr><th>Customer</th><th>Car</th><th>Dates</th><th>Total</th><th>Status</th></tr>
					</thead>
					<tbody>
						<% if (bookings != null) {
							for (Booking booking : bookings) {
						%>
							<tr>
								<td><%=Html.escape(booking.getUserName())%></td>
								<td><%=Html.escape(booking.getCarName())%></td>
								<td><%=booking.getRentDate()%> to <%=booking.getReturnDate()%></td>
								<td><%=CurrencyFormatter.inr(booking.getTotalAmount())%></td>
								<td><span class="status"><%=Html.escape(booking.getStatus())%></span></td>
							</tr>
						<%
							}
						}
						%>
					</tbody>
				</table>
			</div>
		</div>

		<div class="admin-section">
			<h2>Registered users</h2>
			<div class="table-wrap">
				<table>
					<thead>
						<tr><th>Name</th><th>Email</th><th>Role</th></tr>
					</thead>
					<tbody>
						<% if (users != null) {
							for (User listed : users) {
						%>
							<tr>
								<td><%=Html.escape(listed.getName())%></td>
								<td><%=Html.escape(listed.getEmail())%></td>
								<td><%=Html.escape(listed.getRole())%></td>
							</tr>
						<%
							}
						}
						%>
					</tbody>
				</table>
			</div>
		</div>
	</main>
</body>
</html>