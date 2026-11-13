<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard" />
<c:set var="nav" value="dashboard" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <div class="page-head">
            <h1>Dashboard</h1>
            <p>A quick look at the fleet and the latest booking requests.</p>
        </div>

        <div class="stats">
            <div class="stat">
                <div class="value">${totalCars}</div>
                <div class="label">Cars in fleet (${availableCars} available)</div>
            </div>
            <div class="stat">
                <div class="value">${totalCustomers}</div>
                <div class="label">Customers</div>
            </div>
            <div class="stat">
                <div class="value">${totalBookings}</div>
                <div class="label">Bookings</div>
            </div>
            <div class="stat ${pendingBookings > 0 ? 'attention' : ''}">
                <div class="value">${pendingBookings}</div>
                <div class="label">Waiting for a decision</div>
            </div>
            <div class="stat">
                <div class="value">&#8377;<fmt:formatNumber value="${approvedRevenue}" pattern="#,##0" /></div>
                <div class="label">Approved bookings value</div>
            </div>
        </div>

        <div class="toolbar">
            <a class="btn" href="${ctx}/admin/bookings">Review bookings</a>
            <a class="btn btn-outline" href="${ctx}/admin/cars">Manage cars</a>
        </div>

        <div class="section-head"><h2>Latest bookings</h2></div>
        <c:choose>
            <c:when test="${empty recentBookings}">
                <div class="empty"><p>No bookings yet.</p></div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="table">
                        <thead>
                            <tr><th>Booking ID</th><th>Customer</th><th>Car</th><th class="num">Days</th><th class="num">Total</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${recentBookings}">
                                <tr>
                                    <td><strong>${b.id}</strong></td>
                                    <td><c:out value="${b.customerName}" /></td>
                                    <td><c:out value="${b.carName}" /></td>
                                    <td class="num">${b.days}</td>
                                    <td class="num">&#8377;<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0" /></td>
                                    <td><span class="badge badge-${b.status}">${b.status}</span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
