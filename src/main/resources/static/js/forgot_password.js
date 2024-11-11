document.addEventListener("DOMContentLoaded", function () {
  let isValid = true;
  const form = document.querySelector(".formForgotPassword");

  const emailInput = document.getElementById("email");

  const emailError = document.getElementById("emailError");


  form.addEventListener("submit", (e) => {
    isValid = true;
    e.preventDefault();

    const email = emailInput.value;
    if (!email) {
      isValid = false;
      emailError.textContent = "The email is required.";
      emailError.style.display = "block";
    } else {
       emailError.style.display = "none";
    }

    if (isValid) {
      form.submit();
    }
  });
});
