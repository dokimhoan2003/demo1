document.addEventListener("DOMContentLoaded", function () {
  let isValid = true;
  const form = document.querySelector(".formLogin");

  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");

  const emailError = document.getElementById("emailError");
  const passwordError = document.getElementById("passwordError");

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

    const password = passwordInput.value;
    if (!password) {
      isValid = false;
      passwordError.textContent = "The password is required.";
      passwordError.style.display = "block";
    } else {
      passwordError.style.display = "none";
    }



    if (isValid) {
      form.submit();
    }
  });
});
