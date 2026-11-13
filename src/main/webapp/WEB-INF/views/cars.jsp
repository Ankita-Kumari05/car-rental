<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Cars" />
<c:set var="nav" value="cars" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <div class="page-head">
            <h1>Choose your car</h1>
            <p>Every car below is available to book. Prices are per day.</p>
        </div>

        <%@ include file="/WEB-INF/views/messages.jspf" %>

        <div class="chips">
            <a class="chip ${empty selectedType ? 'active' : ''}" href="${ctx}/cars">All</a>
            <c:forTokens items="Hatchback,Sedan,SUV,MUV" delims="," var="t">
                <a class="chip ${selectedType == t ? 'active' : ''}" href="${ctx}/cars?type=${t}">${t}</a>
            </c:forTokens>
        </div>

        <c:choose>
            <c:when test="${empty cars}">
                <div class="empty">
                    <p>No cars match this filter right now.</p>
                    <a class="btn btn-outline" href="${ctx}/cars">Show all cars</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="car-grid">
                    <c:forEach var="car" items="${cars}">
                        <%@ include file="/WEB-INF/views/car-card.jspf" %>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
