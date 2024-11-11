document.addEventListener("DOMContentLoaded", function () {
  let isValid = true;
  const form = document.querySelector(".formUpdatePassword");

  const passwordInput = document.getElementById("password");

  const passwordError = document.getElementById("passwordError");


  form.addEventListener("submit", (e) => {
    isValid = true;
    e.preventDefault();

    const password = passwordInput.value;
    const passwordRegex = /^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?":{}|<>]).{8,}$/;
    if (!password) {
        isValid = false;
        passwordError.textContent = "The password is required.";
        passwordError.style.display = "block";
    } else if(!passwordRegex.test(password)) {
        isValid = false;
        passwordError.textContent = "Password includes 8 characters, uppercase letters, numbers and special characters";
        passwordError.style.display = "block";
    }else {
        passwordError.style.display = "none";
    }

    if (isValid) {
      form.submit();
    }
  });
});
