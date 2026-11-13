<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${car.name}" />
<c:set var="nav" value="cars" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <a class="back-link" href="${ctx}/cars">&larr; All cars</a>

        <div class="two-col">
            <div class="big-media"><%@ include file="/WEB-INF/views/car-image.jspf" %></div>

            <div>
                <h1><c:out value="${car.name}" /></h1>
                <p class="detail-price">&#8377;<fmt:formatNumber value="${car.pricePerDay}" pattern="#,##0" /> <small>per day</small></p>
                <p><c:out value="${car.description}" /></p>

                <table class="spec-table">
                    <tr><th>Type</th><td><c:out value="${car.type}" /></td></tr>
                    <tr><th>Fuel</th><td><c:out value="${car.fuel}" /></td></tr>
                    <tr><th>Transmission</th><td><c:out value="${car.transmission}" /></td></tr>
                    <tr><th>Seats</th><td>${car.seats}</td></tr>
                    <tr>
                        <th>Availability</th>
                        <td>
                            <c:choose>
                                <c:when test="${car.available}"><span class="badge badge-ok">Available</span></c:when>
                                <c:otherwise><span class="badge badge-off">Not available</span></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>

                <c:choose>
                    <c:when test="${car.available}">
                        <a class="btn" href="${ctx}/booking?carId=${car.id}">Book this car</a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn" disabled>Not available</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
