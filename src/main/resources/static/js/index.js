document.addEventListener("DOMContentLoaded", function () {
  var socket = new SockJS("/ws");
  var stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log("Connected: " + frame);

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

  document.querySelectorAll(".bi-calendar3").forEach((icon, index) => {
    icon.addEventListener("click", function () {
      const dateInput =
        index === 0
          ? document.getElementById("fromCreateAt")
          : document.getElementById("toCreateAt");
      dateInput.focus();
    });
  });

  document
    .getElementById("fromCreateAt")
    .addEventListener("change", function (e) {
      const date = new Date(e.target.value);
      if (e.target.value) {
        const formattedDate = date
          .toLocaleDateString("ja-JP", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
          })
          .replace(/\//g, "-");
        document.getElementById("create_at_from").value = formattedDate;
      } else {
        document.getElementById("create_at_from").value = "";
      }
    });

  document
    .getElementById("toCreateAt")
    .addEventListener("change", function (e) {
      const date = new Date(e.target.value);
      if (e.target.value) {
        const formattedDate = date
          .toLocaleDateString("ja-JP", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
          })
          .replace(/\//g, "-");
        document.getElementById("create_at_to").value = formattedDate;
      } else {
        document.getElementById("create_at_to").value = "";
      }
    });

  let productId;
  document.querySelectorAll(".btn-delete").forEach((button) => {
    button.addEventListener("click", function () {
      productId = parseInt(this.getAttribute("data-id"));
      console.log(productId);
    });
  });
  document
    .getElementById("btnConfirmDelete")
    .addEventListener("click", async function () {
      try {
        const response = await fetch(
          `http://192.84.103.230:9898/products/${productId}/confirm-delete`
        );
        if (!response.ok) throw new Error("Network response was not ok");
        const result = await response.json();
        if (result.message === "Delete Successfully") {
          location.reload();
        } else {
          alert("Xóa không thành công");
        }
      } catch (error) {
        console.error("Error delete:", error);
      }
    });
});
