<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Create an account" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <div class="auth-wrap">
            <div class="page-head">
                <h1>Create an account</h1>
                <p>Register once, then book any car in a few clicks.</p>
            </div>

            <%@ include file="/WEB-INF/views/messages.jspf" %>

            <form class="form-card" id="registerForm" method="post" action="${ctx}/register">
                <div class="field">
                    <label for="name">Full name</label>
                    <input type="text" id="name" name="name" value="<c:out value='${formName}' />" required autofocus>
                </div>
                <div class="field">
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email" value="<c:out value='${formEmail}' />" required>
                </div>
                <div class="field">
                    <label for="phone">Phone number</label>
                    <input type="text" id="phone" name="phone" value="<c:out value='${formPhone}' />" required>
                </div>
                <div class="field-row">
                    <div class="field">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" minlength="6" required>
                    </div>
                    <div class="field">
                        <label for="confirm">Confirm password</label>
                        <input type="password" id="confirm" name="confirm" minlength="6" required>
                    </div>
                </div>
                <button type="submit" class="btn btn-block">Create account</button>
                <p class="form-foot">Already registered? <a href="${ctx}/login">Log in</a></p>
            </form>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
