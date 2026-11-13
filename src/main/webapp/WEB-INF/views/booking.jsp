<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Book a car" />
<c:set var="nav" value="cars" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">

        <c:choose>

            <%-- Confirmation: shown after a booking is saved (a "booking" object exists) --%>
            <c:when test="${not empty booking}">
                <div class="auth-wrap" style="max-width:560px">
                    <div class="page-head">
                        <h1>Booking request sent</h1>
                        <p>Our team will review it shortly. Keep your booking ID for reference.</p>
                    </div>
                    <div class="form-card">
                        <table class="summary">
                            <tr><th>Booking ID</th><td><strong>${booking.id}</strong></td></tr>
                            <tr><th>Customer name</th><td><c:out value="${booking.customerName}" /></td></tr>
                            <tr><th>Car</th><td><c:out value="${booking.carName}" /></td></tr>
                            <tr><th>Number of days</th><td>${booking.days}</td></tr>
                            <tr><th>Price per day</th><td>&#8377;<fmt:formatNumber value="${booking.pricePerDay}" pattern="#,##0" /></td></tr>
                            <tr class="total"><th>Total amount</th><td>&#8377;<fmt:formatNumber value="${booking.totalAmount}" pattern="#,##0" /></td></tr>
                            <tr><th>Status</th><td><span class="badge badge-${booking.status}">${booking.status}</span></td></tr>
                        </table>
                        <div class="actions">
                            <a class="btn" href="${ctx}/my-bookings">View my bookings</a>
                            <a class="btn btn-outline" href="${ctx}/cars">Browse more cars</a>
                        </div>
                    </div>
                </div>
            </c:when>

            <%-- Booking form: shown when a car was selected --%>
            <c:otherwise>
                <a class="back-link" href="${ctx}/car-details?id=${car.id}">&larr; Back to car details</a>
                <div class="page-head">
                    <h1>Book <c:out value="${car.name}" /></h1>
                    <p>Enter how many days you need the car.</p>
                </div>

                <%@ include file="/WEB-INF/views/messages.jspf" %>

                <div class="two-col">
                    <div class="form-card">
                        <div class="big-media" style="margin-bottom:18px"><%@ include file="/WEB-INF/views/car-image.jspf" %></div>
                        <table class="summary" style="margin-bottom:0">
                            <tr><th>Car</th><td><c:out value="${car.name}" /></td></tr>
                            <tr><th>Type</th><td><c:out value="${car.type}" />, <c:out value="${car.fuel}" />, <c:out value="${car.transmission}" /></td></tr>
                            <tr><th>Price per day</th><td>&#8377;<fmt:formatNumber value="${car.pricePerDay}" pattern="#,##0" /></td></tr>
                        </table>
                    </div>

                    <form class="form-card" method="post" action="${ctx}/booking">
                        <input type="hidden" name="carId" value="${car.id}">
                        <div class="field">
                            <label>Customer</label>
                            <input type="text" value="<c:out value='${sessionScope.user.name}' />" readonly>
                        </div>
                        <div class="field">
                            <label for="days">Number of days</label>
                            <input type="number" id="days" name="days" min="1" max="30" value="1" required>
                            <div class="hint">Between 1 and 30 days.</div>
                        </div>
                        <div class="total-box">
                            <span>Total amount</span>
                            <strong id="total" data-price="${car.pricePerDay}">&#8377;<fmt:formatNumber value="${car.pricePerDay}" pattern="#,##0" /></strong>
                        </div>
                        <button type="submit" class="btn btn-block">Confirm booking</button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
