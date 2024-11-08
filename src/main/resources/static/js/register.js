document.addEventListener("DOMContentLoaded", function () {
  let isValid = true;
  const form = document.querySelector(".formRegister");
  const firstNameInput = document.getElementById("firstName");
  const lastNameInput = document.getElementById("lastName");
  const emailInput = document.getElementById("email");
  const phoneInput = document.getElementById("phone");
  const passwordInput = document.getElementById("password");
  const passwordConfirmInput = document.getElementById("passwordConfirm");

  const firstNameError = document.getElementById("firstNameError");
  const lastNameError = document.getElementById("lastNameError");
  const emailError = document.getElementById("emailError");
  const phoneError = document.getElementById("phoneError");
  const passwordError = document.getElementById("passwordError");
  const passwordConfirmError = document.getElementById("passwordConfirmError");

  form.addEventListener("submit", (e) => {
    isValid = true;
    e.preventDefault();

    const firstName = firstNameInput.value;
    if (!firstName) {
      isValid = false;
      firstNameError.textContent = "The first name is required.";
      firstNameError.style.display = "block";
    } else if(firstName.length > 25) {
      isValid = false;
      firstNameError.textContent = "The first name must be between 1 and 25 characters";
      firstNameError.style.display = "block";
    }else {
      firstNameError.style.display = "none";
    }

    const lastName = lastNameInput.value;
    if (!lastName) {
      isValid = false;
      lastNameError.textContent = "The last name is required.";
      lastNameError.style.display = "block";
    }else if(lastName.length > 25) {
      isValid = false;
      lastNameError.textContent = "The last name must be between 1 and 25 characters";
      lastNameError.style.display = "block";
    } else {
      lastNameError.style.display = "none";
    }

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

    const passwordConfirm = passwordConfirmInput.value;
    if(!passwordConfirm) {
        isValid = false;
        passwordConfirmError.textContent = "The password confirm is required.";
        passwordConfirmError.style.display = "block";
    }else if(passwordConfirm !== password) {
        isValid = false;
        passwordConfirmError.textContent = "The password confirm no matches with password";
        passwordConfirmError.style.display = "block";
    }else {
        passwordConfirmError.style.display = "none";
    }

    const email = emailInput.value;
    const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z]{2,}$/;
    if (!email) {
      isValid = false;
      emailError.textContent = "The email is required.";
      emailError.style.display = "block";
    } else if(!emailRegex.test(email)) {
      isValid = false;
      emailError.textContent = "Invalid email format";
      emailError.style.display = "block";
    }else {
      emailError.style.display = "none";
    }

    const phone = phoneInput.value;
    const phoneRegex = /^0\d{0,9}$/;

    if (!phone) {
      isValid = false;
      phoneError.textContent = "The phone is required.";
      phoneError.style.display = "block";
    }else if(!phoneRegex.test(phone)) {
      isValid = false;
      phoneError.textContent = "Phone maximum 10 number";
      phoneError.style.display = "block";
    } else {
      phoneError.style.display = "none";
    }

    if (isValid) {
      form.submit();
    }
  });
});
