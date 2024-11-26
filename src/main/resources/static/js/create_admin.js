document.addEventListener("DOMContentLoaded", function () {
  // connect client and server use SockJS
  var socket = new SockJS("/ws");
  var stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log("Connected: " + frame);

    stompClient.subscribe("/topic/notificationCreateCategory", (message) => {
      var notification = JSON.parse(message.body).content;
      showNotification(notification);
    });
    stompClient.subscribe("/topic/notificationUpdateCategory", (message) => {
      var notification = JSON.parse(message.body).content;
      showNotification(notification);
    });
    stompClient.subscribe("/topic/notificationDeleteCategory", (message) => {
      var notification = JSON.parse(message.body).content;
      showNotification(notification);
    });
    stompClient.subscribe("/topic/notificationCreateProduct", (message) => {
      var notification = JSON.parse(message.body).content;
      showNotification(notification);
    });
    stompClient.subscribe("/topic/notificationUpdateProduct", (message) => {
      var notification = JSON.parse(message.body).content;
      showNotification(notification);
    });
    stompClient.subscribe("/topic/notificationDeleteProduct", (message) => {
      var notification = JSON.parse(message.body).content;
      showNotification(notification);
    });
  });

  const showNotification = (notification) => {
    var badge = document.querySelector(".badge-number");
    if (badge.textContent) {
      badge.textContent = parseInt(badge.textContent) + 1;
    } else {
      badge.textContent = 1;
    }

    if (badge.classList.contains("d-none")) {
      badge.classList.remove("d-none");
    }

    var notificationList = document.querySelector(".notifications .separation");
    var newNotification = `
                                <li class="notification-item">
                                    <i class="bi bi-info-circle text-primary"></i>
                                    <div>
                                        <h4>Notification</h4>
                                        <p>${notification}</p>
                                        <p>Just now</p>
                                    </div>
                                </li>
                            `;
    notificationList.insertAdjacentHTML("afterend", newNotification);
  };
  var badge = document.querySelector(".badge-number");
  var bell = document.querySelector(".bell");
  bell.addEventListener("click", () => {
    badge.classList.add("d-none");
  });

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
    } else if (firstName.length > 25) {
      isValid = false;
      firstNameError.textContent =
        "The first name must be between 1 and 25 characters";
      firstNameError.style.display = "block";
    } else {
      firstNameError.style.display = "none";
    }

    const lastName = lastNameInput.value;
    if (!lastName) {
      isValid = false;
      lastNameError.textContent = "The last name is required.";
      lastNameError.style.display = "block";
    } else if (lastName.length > 25) {
      isValid = false;
      lastNameError.textContent =
        "The last name must be between 1 and 25 characters";
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
    } else if (!emailRegex.test(email)) {
      isValid = false;
      emailError.textContent = "Enter a valid email address";
      emailError.style.display = "block";
    } else {
      emailError.style.display = "none";
    }

    const phone = phoneInput.value;
    const phoneRegex = /^0\d{0,9}$/;

    if (!phone) {
      isValid = false;
      phoneError.textContent = "The phone is required.";
      phoneError.style.display = "block";
    } else if (!phoneRegex.test(phone)) {
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
