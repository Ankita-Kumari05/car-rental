// Basic JavaScript used on a few pages. No libraries needed.

document.addEventListener("DOMContentLoaded", function () {

    // 1. Booking page: show the total amount while the customer types the number of days.
    var daysInput = document.getElementById("days");
    var totalBox = document.getElementById("total");
    if (daysInput && totalBox) {
        var pricePerDay = parseFloat(totalBox.getAttribute("data-price"));

        function updateTotal() {
            var days = parseInt(daysInput.value, 10);
            if (isNaN(days) || days < 1) {
                days = 0;
            }
            totalBox.textContent = "\u20B9" + (days * pricePerDay).toLocaleString("en-IN");
        }

        daysInput.addEventListener("input", updateTotal);
        updateTotal();
    }

    // 2. Any form with a data-confirm attribute asks "Are you sure?" before it is sent.
    var confirmForms = document.querySelectorAll("form[data-confirm]");
    confirmForms.forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!window.confirm(form.getAttribute("data-confirm"))) {
                event.preventDefault();
            }
        });
    });

    // 3. Register page: check that both passwords match before sending the form.
    var registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", function (event) {
            var password = document.getElementById("password").value;
            var confirm = document.getElementById("confirm").value;
            if (password !== confirm) {
                event.preventDefault();
                alert("The two passwords do not match.");
            }
        });
    }
});
