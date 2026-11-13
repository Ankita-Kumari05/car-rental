<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My bookings" />
<c:set var="nav" value="my-bookings" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <div class="page-head">
            <h1>My bookings</h1>
            <p>Track your requests and cancel one if your plans change.</p>
        </div>

        <%@ include file="/WEB-INF/views/messages.jspf" %>

        <c:choose>
            <c:when test="${empty bookings}">
                <div class="empty">
                    <p>You have not booked a car yet.</p>
                    <a class="btn" href="${ctx}/cars">Browse cars</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Booking ID</th><th>Car</th><th>Date</th><th class="num">Days</th>
                                <th class="num">Price / day</th><th class="num">Total</th><th>Status</th><th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${bookings}">
                                <tr>
                                    <td><strong>${b.id}</strong></td>
                                    <td><c:out value="${b.carName}" /></td>
                                    <td>${b.bookingDate}</td>
                                    <td class="num">${b.days}</td>
                                    <td class="num">&#8377;<fmt:formatNumber value="${b.pricePerDay}" pattern="#,##0" /></td>
                                    <td class="num"><strong>&#8377;<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0" /></strong></td>
                                    <td><span class="badge badge-${b.status}">${b.status}</span></td>
                                    <td>
                                        <c:if test="${b.cancellable}">
                                            <form method="post" action="${ctx}/cancel-booking" data-confirm="Cancel booking ${b.id}?">
                                                <input type="hidden" name="id" value="${b.id}">
                                                <button type="submit" class="btn btn-sm btn-danger">Cancel</button>
                                            </form>
                                        </c:if>
                                    </td>
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
