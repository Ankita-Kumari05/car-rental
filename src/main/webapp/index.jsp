<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- The home page needs the featured cars, which CarServlet (/home) loads. If this page was opened directly, ask the servlet first. --%>
<c:if test="${featuredCars == null}">
    <jsp:forward page="/home" />
</c:if>
<c:set var="nav" value="home" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <section class="hero">
        <div class="container hero-inner">
            <div>
                <h1>Self-drive cars for every kind of trip</h1>
                <p>Choose a car, enter how many days you need it, and send your booking request in under a minute. Hatchbacks, sedans, SUVs and seven-seaters at clear daily rates.</p>
                <div class="hero-actions">
                    <a class="btn" href="${ctx}/cars">Browse cars</a>
                    <c:if test="${empty sessionScope.user}">
                        <a class="btn btn-ghost" href="${ctx}/register">Create an account</a>
                    </c:if>
                </div>
            </div>
            <div class="hero-art" aria-hidden="true">
                <svg viewBox="0 0 240 100">
                    <path d="M10 70 L10 58 Q10 52 18 50 L52 44 L78 22 Q82 18 90 18 L150 18 Q160 18 166 24 L186 44 L218 50 Q230 52 230 62 L230 70 Q230 74 226 74 L14 74 Q10 74 10 70 Z" fill="#F2A900" />
                    <polygon points="68,43 88,26 126,26 126,43" fill="#17335C" />
                    <polygon points="133,26 152,26 174,43 133,43" fill="#17335C" />
                    <circle cx="60" cy="76" r="14" fill="#06122A" />
                    <circle cx="60" cy="76" r="6" fill="#9FB1CB" />
                    <circle cx="182" cy="76" r="14" fill="#06122A" />
                    <circle cx="182" cy="76" r="6" fill="#9FB1CB" />
                </svg>
            </div>
        </div>
    </section>

    <section class="section">
        <div class="container">
            <div class="section-head">
                <h2>Available now</h2>
                <a href="${ctx}/cars">See all cars</a>
            </div>
            <c:choose>
                <c:when test="${empty featuredCars}">
                    <div class="empty"><p>No cars are available right now. Please check again later.</p></div>
                </c:when>
                <c:otherwise>
                    <div class="car-grid">
                        <c:forEach var="car" items="${featuredCars}">
                            <%@ include file="/WEB-INF/views/car-card.jspf" %>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <section class="section section-tint">
        <div class="container">
            <h2>How booking works</h2>
            <div class="steps">
                <div class="step">
                    <h3>1. Pick a car</h3>
                    <p>Browse the fleet and open a car to see its seats, fuel type and daily price.</p>
                </div>
                <div class="step">
                    <h3>2. Enter the number of days</h3>
                    <p>The total is the daily price multiplied by your days. You see it before you confirm.</p>
                </div>
                <div class="step">
                    <h3>3. Wait for approval</h3>
                    <p>Our team approves the request. You can follow its status, or cancel it, under My bookings.</p>
                </div>
            </div>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
