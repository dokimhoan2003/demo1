document.addEventListener("DOMContentLoaded", function () {
  let isValid = true;
  const form = document.querySelector(".formCreateAdmin");
  const firstNameInput = document.getElementById("firstName");
  const lastNameInput = document.getElementById("lastName");
  const emailInput = document.getElementById("email");
  const phoneInput = document.getElementById("phone");


  const firstNameError = document.getElementById("firstNameError");
  const lastNameError = document.getElementById("lastNameError");
  const emailError = document.getElementById("emailError");
  const phoneError = document.getElementById("phoneError");

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


    const email = emailInput.value;
    const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z]{2,}$/;
    if (!email) {
      isValid = false;
      emailError.textContent = "The email is required.";
      emailError.style.display = "block";
    } else if(!emailRegex.test(email)) {
      isValid = false;
      emailError.textContent = "Enter a valid email address";
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
      phoneError.textContent = "Enter a valid phone number";
      phoneError.style.display = "block";
    } else {
      phoneError.style.display = "none";
    }

    if (isValid) {
      form.submit();
    }
  });
});
