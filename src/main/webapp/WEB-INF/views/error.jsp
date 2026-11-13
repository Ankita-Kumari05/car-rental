<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Something went wrong" />
<%@ include file="/WEB-INF/views/header.jspf" %>
<main>
    <div class="container page">
        <div class="empty">
            <h2>We could not open that page</h2>
            <p>The page may not exist, or something went wrong on our side.</p>
            <a class="btn" href="${ctx}/">Back to home</a>
        </div>
    </div>
</main>
<%@ include file="/WEB-INF/views/footer.jspf" %>
