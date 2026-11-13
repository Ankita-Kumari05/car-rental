<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Log in" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <div class="auth-wrap">
            <div class="page-head">
                <h1>Log in</h1>
                <p>Use your e-mail and password to book cars or manage the fleet.</p>
            </div>

            <%@ include file="/WEB-INF/views/messages.jspf" %>

            <form class="form-card" method="post" action="${ctx}/login">
                <div class="field">
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email" value="<c:out value='${formEmail}' />" required autofocus>
                </div>
                <div class="field">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-block">Log in</button>
                <p class="form-foot">New here? <a href="${ctx}/register">Create an account</a></p>
            </form>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
