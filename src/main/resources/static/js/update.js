document.addEventListener("DOMContentLoaded", function () {
  let isValid = true;

  const form = document.querySelector("form");
  const nameInput = document.getElementById("nameInput");
  const descriptionInput = document.getElementById("desInput");
  const brandInput = document.getElementById("brandInput");
  const priceInput = document.getElementById("priceInput");
  const colorSelects = document.getElementsByName("color");
  const thumbnailFileInput = document.getElementById("thumbnailFile");
  const initialNameValue = nameInput.value;
  const imageFilesInput = document.getElementById("imageFiles");

  const nameError = document.getElementById("nameError");
  const desError = document.getElementById("desError");
  const brandError = document.getElementById("brandError");
  const priceError = document.getElementById("priceError");
  const colorError = document.getElementById("colorError");
  const thumbnailError = document.getElementById("thumbnailError");
  const imageFilesError = document.getElementById("imageFilesError");

  let listAddImages = [];
  let listDeleteImages = [];
  const maxSize = 10 * 1024 * 1024;

  document.getElementById("btnUpload").addEventListener("click", () => {
    document.getElementById("imageFiles").click();
  });

  // Xử lý xóa ảnh cũ khi nhấn nút "Xóa" trên ảnh cũ
  document.querySelectorAll(".image-container .btn").forEach((btn) => {
    btn.addEventListener("click", function () {
      const fileName = btn.getAttribute("image-index");
      listDeleteImages.push(fileName);
      btn.closest(".image-container").remove();
    });
  });





      // chọn ảnh mới từ file input
      imageFilesInput.addEventListener("change", function (event) {
      const files = event.target.files;
      const previewContainer = document.getElementById("imageDetailsPreview");

      let qualityImageCurrent = document.querySelectorAll("#imageDetailsPreview .image-container").length;
      if(qualityImageCurrent + files.length > 6) {
           isValid = false;
           imageFilesError.style.display = "block";
           imageFilesError.textContent = "Number image details must be <= 6";
           event.target.value = "";
           return;
      }

      for (let i = 0; i < files.length; i++) {
          const file = files[i];
          if(file.size > maxSize) {
                        isValid = false;
                        imageFilesError.style.display = "block";
                        imageFilesError.textContent = `File ${file.name} is too large! Maximum size is 10MB`;
                        event.target.value = "";
                        continue;
                    }else if(files.length > 6) {
                        isValid = false;
                        imageFilesError.style.display = "block";
                        imageFilesError.textContent = "Number image details must be <= 6";
                        event.target.value = "";
                        continue;
                    }else{
                        imageFilesError.style.display = "none";
                    }
                    listAddImages.push(file);

                    // Hiển thị ảnh mới lên giao diện
                    const reader = new FileReader();
                    reader.onload = function (e) {
                      const div = document.createElement("div");
                      div.className = "me-2 mb-2 position-relative image-container";

                      const img = document.createElement("img");
                      img.src = e.target.result;
                      img.className = "img-thumbnail";
                      img.width = 200;
                      img.alt = "Detail Image";

                      const btnRemove = document.createElement("button");
                      btnRemove.className = "btn position-absolute top-0 end-0";
                      btnRemove.style.right = "5px";
                      btnRemove.style.top = "5px";

                      const icon = document.createElement("i");
                      icon.className = "bi bi-x-circle-fill text-info";

                      btnRemove.appendChild(icon);
                      btnRemove.addEventListener("click", function () {
                        previewContainer.removeChild(div); // Xóa ảnh khỏi giao diện
                        listAddImages.splice(i, 1); // Xóa ảnh khỏi danh sách
                      });

                      div.appendChild(img);
                      div.appendChild(btnRemove);
                      previewContainer.appendChild(div);
                    };

                    reader.readAsDataURL(file); // Đọc và hiển thị ảnh mới
      }
    });

  const getProductId = () => {
    const url = new URL(window.location.href);
    const pathname = url.pathname;
    const segments = pathname.split("/");
    const id = segments[segments.length - 1];
    return id;
  }

  // Khi submit form, gửi danh sách ảnh cần delete và add
  const createFormData = () => {
    const formData = new FormData(document.querySelector("form"));

    formData.append("id", getProductId());

    listAddImages.forEach((file) => {
      formData.append("listImages", file);
    });

    listDeleteImages.forEach((fileName) => {
      formData.append("listDelete", fileName);
    });

    //     for (let [key, value] of formData.entries()) {
    //            console.log(`${key}: ${value}`);
    //        }

    return formData;
  }

  let selectedThumbnail = [];
  const previewSelectedThumbnail = () => {
    const thumbnailPreview = document.getElementById("thumbnailPreview");
                thumbnailPreview.innerHTML = "";
                selectedThumbnail.forEach((file, index) => {
                  const reader = new FileReader();
                  reader.onload = (e) => {
                    // Khi đọc file hoàn tất
                    const thumbnailElement = document.createElement("div");
                    thumbnailElement.classList.add("me-2", "mb-2", "position-relative");
                    thumbnailElement.innerHTML = `<img src="${e.target.result}" alt="Image" class="img-thumbnail" width="100">`;
                    thumbnailPreview.appendChild(thumbnailElement);
                  };
                  reader.readAsDataURL(file); // đọc file
                });
                const dataTransfer = new DataTransfer();
                selectedThumbnail.forEach((image) => dataTransfer.items.add(image));
                thumbnailFileInput.files[0] = dataTransfer.files[0]; // Cập nhật trường input
                selectedThumbnail.length = 0;
  }

  // Immediate feedback for image file size on file change
  thumbnailFileInput.addEventListener("change", function (e) {
    var thumbnail = e.target.files;
    selectedThumbnail.push(...thumbnail);
    if (thumbnail[0]) {
      if (thumbnail[0].size > maxSize) {
        isValid = false;
        thumbnailError.style.display = "block";
        thumbnailError.textContent = "File is too large! Maximum size is 10MB";
        e.target.value = "";
      } else {
        thumbnailError.style.display = "none";
      }
      previewSelectedThumbnail();
    }
  });

  const validateForm = async () => {
    // Validate name
    const name = nameInput.value;
    if (!name) {
      nameError.textContent = "The name is required.";
      nameError.style.display = "block";
      isValid = false;
    } else if (name.length > 25) {
      nameError.textContent = "The name must be between 1 and 25 characters";
      nameError.style.display = "block";
      isValid = false;
    } else if (name !== initialNameValue) {
      try {
        const response = await fetch(
          `http://192.168.1.220:9898/products/check-name?name=${encodeURIComponent(
            name
          )}`
        );
        const exist = await response.json();
        if (exist) {
          isValid = false;
          nameError.textContent = "The name already exists";
          nameError.style.display = "block";
        } else {
          nameError.style.display = "none";
        }
      } catch (error) {
        isValid = false;
        nameError.textContent =
          "Error checking product name. Please try again.";
        nameError.style.display = "block";
      }
    } else {
      nameError.style.display = "none";
    }

    // Validate price
    const price = priceInput.value;
    if (price < 0) {
      isValid = false;
      priceError.textContent = "Price must be greater than or equal to zero";
      priceError.style.display = "block";
    } else {
      priceError.style.display = "none";
    }

    // Validate description
    const description = descriptionInput.value;
    if (!description) {
      isValid = false;
      desError.textContent = "The description is required.";
      desError.style.display = "block";
    } else if (description.length > 2000) {
      isValid = false;
      desError.textContent =
        "The description must be between 1 and 2000 characters.";
      desError.style.display = "block";
    } else {
      desError.style.display = "none";
    }

    // Validate brand
    const brand = brandInput.value;
    if (!brand) {
      isValid = false;
      brandError.textContent = "The brand is required.";
      brandError.style.display = "block";
    } else if (brand.length > 25) {
      isValid = false;
      brandError.textContent = "The brand must be between 1 and 25 characters.";
      brandError.style.display = "block";
    } else {
      brandError.style.display = "none";
    }

    // Validate color
    let colorSelected = false;
    for (const colorSelect of colorSelects) {
      if (colorSelect.checked) {
        colorSelected = true;
        break;
      }
    }
    if (!colorSelected) {
          isValid = false;
          colorError.textContent = "Please select value";
          colorError.style.display = "block";
        } else {
          colorError.style.display = "none";
        }

    return isValid;
  };

  // Gửi dữ liệu khi form được submit
  form.addEventListener("submit", async function (event) {
      isValid = true;
      event.preventDefault(); // Ngăn chặn form submit theo cách mặc định

      const passValidate = await validateForm();
      if (!passValidate) {
        return;
      }

      const formData = createFormData();



      const id = getProductId();
      try {
        const response = await fetch(
          `http://192.168.1.220:9898/products/update/${id}`,
          {
            method: "POST",
            body: formData,
          }
        );



        if (response.ok) {
           const data = await response.json();
           console.log(data);
           // Điều hướng về danh sách sản phẩm sau khi cập nhật thành công
           window.location.href = "http://192.168.1.220:9898/products"; // URL của trang danh sách sản phẩm
        } else {
          console.error("Cập nhật thất bại.");
        }
      } catch (error) {
        console.error("Err:", error);
      }
    });
});
