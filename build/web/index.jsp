<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>DriveEasy | Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav>
    <a class="brand" href="${pageContext.request.contextPath}/index.jsp">DRIVEEASY</a>
    <div>
        <a href="${pageContext.request.contextPath}/login.jsp">Log in</a>
        <a class="nav-button" href="${pageContext.request.contextPath}/register.jsp">Get started</a>
    </div>
</nav>

<main class="hero">
    <section>
        <p class="eyebrow">CAR RENTAL, REFINED</p>
        <h1>Move through the city on your terms.</h1>
        <p class="lead">A simple, transparent way to find a car that fits your day. Browse our fleet, book in minutes, and keep moving.</p>
        <a class="button" href="${pageContext.request.contextPath}/register.jsp">Create your account <span>-></span></a>
    </section>

    <aside class="hero-stat">
        <strong>24/7</strong>
        <span>Road-ready support</span>
        <hr>
        <strong>03</strong>
        <span>Ways to find your fit</span>
    </aside>
</main>

<footer>DriveEasy Car Rental Management System</footer>

</body>
</html>
