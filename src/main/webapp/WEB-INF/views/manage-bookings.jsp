<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage bookings" />
<c:set var="nav" value="manage-bookings" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <div class="page-head">
            <h1>Manage bookings</h1>
            <p>Approve or reject booking requests. Cancelled bookings cannot be changed.</p>
        </div>

        <%@ include file="/WEB-INF/views/messages.jspf" %>

        <c:choose>
            <c:when test="${empty bookings}">
                <div class="empty"><p>No bookings yet.</p></div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Booking ID</th><th>Customer</th><th>Car</th><th>Date</th><th class="num">Days</th>
                                <th class="num">Price / day</th><th class="num">Total</th><th>Status</th><th>Decision</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${bookings}">
                                <tr>
                                    <td><strong>${b.id}</strong></td>
                                    <td><c:out value="${b.customerName}" /></td>
                                    <td><c:out value="${b.carName}" /></td>
                                    <td>${b.bookingDate}</td>
                                    <td class="num">${b.days}</td>
                                    <td class="num">&#8377;<fmt:formatNumber value="${b.pricePerDay}" pattern="#,##0" /></td>
                                    <td class="num"><strong>&#8377;<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0" /></strong></td>
                                    <td><span class="badge badge-${b.status}">${b.status}</span></td>
                                    <td>
                                        <c:if test="${b.status != 'Cancelled'}">
                                            <div class="actions">
                                                <form method="post" action="${ctx}/admin/bookings/status">
                                                    <input type="hidden" name="id" value="${b.id}">
                                                    <input type="hidden" name="status" value="Approved">
                                                    <button type="submit" class="btn btn-sm" ${b.status == 'Approved' ? 'disabled' : ''}>Approve</button>
                                                </form>
                                                <form method="post" action="${ctx}/admin/bookings/status">
                                                    <input type="hidden" name="id" value="${b.id}">
                                                    <input type="hidden" name="status" value="Rejected">
                                                    <button type="submit" class="btn btn-sm btn-danger" ${b.status == 'Rejected' ? 'disabled' : ''}>Reject</button>
                                                </form>
                                            </div>
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
