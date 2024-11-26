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
});
