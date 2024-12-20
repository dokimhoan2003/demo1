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
                                <i class="bi bi-dot text-primary"></i>
                            </li>
                        `;
    notificationList.insertAdjacentHTML("afterend", newNotification);
  };
  var bell = document.querySelector(".bell");
  var badge = document.querySelector(".badge-number");
  bell.addEventListener("click", () => {
    badge.textContent = "";
    badge.classList.add("d-none");
  });

  let categoryId;
  document.querySelectorAll(".btn-delete").forEach((button) => {
    button.addEventListener("click", function () {
      categoryId = parseInt(this.getAttribute("data-id"));
    });
  });

  document
    .getElementById("btnConfirmDelete")
    .addEventListener("click", async function () {
      try {
        const response = await fetch(
          `http://192.84.103.230:9898/categories/${categoryId}/confirm-delete`
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
