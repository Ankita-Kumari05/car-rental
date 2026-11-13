<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage cars" />
<c:set var="nav" value="manage-cars" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<main>
    <div class="container page">
        <div class="page-head">
            <h1>Manage cars</h1>
            <p>Add a car, change its details or price, or remove it from the fleet.</p>
        </div>

        <%@ include file="/WEB-INF/views/messages.jspf" %>

        <div class="admin-split">

            <%-- Left: all cars --%>
            <div class="table-wrap">
                <table class="table">
                    <thead>
                        <tr><th>Car</th><th>Type</th><th class="num">Price / day</th><th>Status</th><th></th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="car" items="${cars}">
                            <tr>
                                <td>
                                    <span class="mini-thumb" style="background:<c:out value='${car.color}' />"></span>
                                    <strong><c:out value="${car.name}" /></strong>
                                </td>
                                <td><c:out value="${car.type}" /></td>
                                <td class="num">&#8377;<fmt:formatNumber value="${car.pricePerDay}" pattern="#,##0" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${car.available}"><span class="badge badge-ok">Available</span></c:when>
                                        <c:otherwise><span class="badge badge-off">Hidden</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="actions">
                                        <a class="btn btn-sm btn-outline" href="${ctx}/admin/cars?edit=${car.id}">Edit</a>
                                        <form method="post" action="${ctx}/admin/cars/delete" data-confirm="Delete <c:out value='${car.name}' />? This cannot be undone.">
                                            <input type="hidden" name="id" value="${car.id}">
                                            <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <%-- Right: add / edit form --%>
            <form class="form-card" method="post" action="${ctx}/admin/cars/save">
                <h2>${empty editCar ? 'Add a car' : 'Edit car'}</h2>
                <input type="hidden" name="id" value="${editCar.id}">

                <div class="field">
                    <label for="name">Car name</label>
                    <input type="text" id="name" name="name" value="<c:out value='${editCar.name}' />" required>
                </div>
                <div class="field-row">
                    <div class="field">
                        <label for="type">Type</label>
                        <select id="type" name="type">
                            <c:forTokens items="Hatchback,Sedan,SUV,MUV" delims="," var="t">
                                <option value="${t}" ${editCar.type == t ? 'selected' : ''}>${t}</option>
                            </c:forTokens>
                        </select>
                    </div>
                    <div class="field">
                        <label for="fuel">Fuel</label>
                        <select id="fuel" name="fuel">
                            <c:forTokens items="Petrol,Diesel,CNG,Electric" delims="," var="f">
                                <option value="${f}" ${editCar.fuel == f ? 'selected' : ''}>${f}</option>
                            </c:forTokens>
                        </select>
                    </div>
                </div>
                <div class="field-row">
                    <div class="field">
                        <label for="transmission">Transmission</label>
                        <select id="transmission" name="transmission">
                            <c:forTokens items="Manual,Automatic" delims="," var="m">
                                <option value="${m}" ${editCar.transmission == m ? 'selected' : ''}>${m}</option>
                            </c:forTokens>
                        </select>
                    </div>
                    <div class="field">
                        <label for="seats">Seats</label>
                        <input type="number" id="seats" name="seats" min="1" max="12" value="${empty editCar ? 5 : editCar.seats}" required>
                    </div>
                </div>
                <div class="field-row">
                    <div class="field">
                        <label for="pricePerDay">Price per day (&#8377;)</label>
                        <fmt:formatNumber var="priceValue" value="${editCar.pricePerDay}" pattern="0" />
                        <input type="number" id="pricePerDay" name="pricePerDay" min="1" value="${empty editCar ? '' : priceValue}" required>
                    </div>
                    <div class="field">
                        <label for="color">Picture colour</label>
                        <input type="color" id="color" name="color" value="${empty editCar ? '#2D5BA6' : editCar.color}">
                    </div>
                </div>
                <div class="field">
                    <label for="image">Photo file name (optional)</label>
                    <input type="text" id="image" name="image" value="<c:out value='${editCar.image}' />" placeholder="swift.jpg">
                    <div class="hint">Put the file in webapp/images. Leave empty to show the drawn car.</div>
                </div>
                <div class="field">
                    <label for="description">Description</label>
                    <textarea id="description" name="description"><c:out value="${editCar.description}" /></textarea>
                </div>
                <label class="check">
                    <input type="checkbox" name="available" ${empty editCar || editCar.available ? 'checked' : ''}>
                    Available for booking
                </label>

                <div class="actions">
                    <button type="submit" class="btn">${empty editCar ? 'Add car' : 'Save changes'}</button>
                    <c:if test="${not empty editCar}">
                        <a class="btn btn-outline" href="${ctx}/admin/cars">Cancel</a>
                    </c:if>
                </div>
            </form>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jspf" %>
