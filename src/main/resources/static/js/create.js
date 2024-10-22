document.addEventListener("DOMContentLoaded", function () {
  let isValid = true;

  const form = document.querySelector("form");
  const nameInput = document.getElementById("nameInput");
  const descriptionInput = document.getElementById("desInput");
  const brandInput = document.getElementById("brandInput");
  const priceInput = document.getElementById("priceInput");
  const colorSelects = document.getElementsByName("color");
  const thumbnailFileInput = document.getElementById("thumbnailFile");
  const imageFilesInput = document.getElementById("imageFiles");

  const nameError = document.getElementById("nameError");
  const desError = document.getElementById("desError");
  const brandError = document.getElementById("brandError");
  const priceError = document.getElementById("priceError");
  const colorError = document.getElementById("colorError");
  const thumbnailError = document.getElementById("thumbnailError");
  const imageFilesError = document.getElementById("imageFilesError");

  const maxSize = 10 * 1024 * 1024; // 10MB for file size
  const validFileTypes = ["image/jpeg", "image/png", "image/jpg"];

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
                thumbnailElement.innerHTML = `<img src="${e.target.result}" alt="Image" class="img-thumbnail" width="100">
                    `;
                thumbnailPreview.appendChild(thumbnailElement);

//                thumbnailElement
//                  .querySelector("#btnRemoveThumbnail")
//                  .addEventListener("click", function () {
//                    const fileIndex = parseInt(this.getAttribute("data-index"));
//                    selectedThumbnail.splice(fileIndex, 1); // Xóa file khỏi mảng
//                    previewSelectedThumbnail(); // Cập nhật lại giao diện
//                  });
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

  document.getElementById("btnUpload").addEventListener("click", () => {
    document.getElementById("imageFiles").click();
  });

  let selectedDetailImages = [];
  imageFilesInput.addEventListener("change", function (e) {
    var files = e.target.files;

    const newFiles = Array.from(files);
    selectedDetailImages.push(...newFiles);

    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      if (file.size > maxSize) {
        isValid = false;
        imageFilesError.style.display = "block";
        imageFilesError.textContent = `File ${file.name} is too large! Maximum size is 10MB`;
        e.target.value = "";
        break;
      } else {
        imageFilesError.style.display = "none";
      }
    }
    previewSelectedImages();

  });
  const previewSelectedImages = () => {
    const imageDetailsPreview = document.getElementById("imageDetailsPreview");
    imageDetailsPreview.innerHTML = "";
    selectedDetailImages.forEach((file, index) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        // Khi đọc file hoàn tất
        const imgDetailsElement = document.createElement("div");
        imgDetailsElement.classList.add("me-2", "mb-2", "position-relative");
        imgDetailsElement.innerHTML = `<img src="${e.target.result}" alt="Image" class="img-thumbnail" width="200">
            <button type="button" id="btnRemove" class="btn position-absolute top-0 end-0" data-index="${index}"><i class="bi bi-x-circle-fill text-info"></i>

</button>`;
        imageDetailsPreview.appendChild(imgDetailsElement);
        console.log(imageDetailsPreview);
        // sự kiện delete image
        imgDetailsElement
          .querySelector("#btnRemove")
          .addEventListener("click", function () {
            const fileIndex = parseInt(this.getAttribute("data-index"));
            selectedDetailImages.splice(fileIndex, 1); // Xóa file khỏi mảng
            previewSelectedImages(); // Cập nhật lại giao diện
          });
      };
      reader.readAsDataURL(file); // đọc file
    });
    const dataTransfer = new DataTransfer();
    selectedDetailImages.forEach((image) => dataTransfer.items.add(image));
    imageFilesInput.files = dataTransfer.files; // Cập nhật trường input
  };

  // Form submission validation (including image file size)
  form.addEventListener("submit", async function (event) {
    isValid = true;
    event.preventDefault();

    // Validate name field
    const name = nameInput.value;
    if (!name) {
      isValid = false;
      nameError.textContent = "The name is required.";
      nameError.style.display = "block";
    } else if (name.length > 25) {
      isValid = false;
      nameError.textContent = "The name must be between 1 and 25 characters";
      nameError.style.display = "block";
    } else {
      try {
        const response = await fetch(
          `http://192.84.103.230:9898/products/check-name?name=${encodeURIComponent(
            name
          )}`
        );
        const exist = await response.json();
        if (exist) {
          isValid = false;
          nameError.textContent = "The name already exist";
          nameError.style.display = "block";
        } else {
          nameError.style.display = "none";
        }
      } catch (error) {
        isValid = false;
        nameError.textContent = "Error checking product name. Please try again";
        nameError.style.display = "block";
      }
    }

    const price = priceInput.value;
    if (price < 0) {
      isValid = false;
      priceError.textContent = "Price must be greater than or equal zero";
      priceError.style.display = "block";
    } else {
      priceError.style.display = "none";
    }

    // Validate description field
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

    // Validate brand field
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

    //Validate color field
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

    // Validate thumbnail file
    const thumbnail = thumbnailFileInput.files[0];

    if (!thumbnail) {
      isValid = false;
      thumbnailError.textContent = "The thumbnail image is required";
      thumbnailError.style.display = "block";
    } else {
      if (!validFileTypes.includes(thumbnail.type)) {
        isValid = false;
        thumbnailError.textContent =
          "File valid with extension.jpg/.jpeg/.png.";
        thumbnailError.style.display = "block";
      } else if (thumbnail.size > maxSize) {
        isValid = false;
        thumbnailError.textContent = "File is too large! Maximum size is 10MB";
        thumbnailError.style.display = "block";
      } else {
        thumbnailError.style.display = "none";
      }
    }

    // validate images file
    const files = imageFilesInput.files;
    if (files.length > 6) {
      isValid = false;
      imageFilesError.style.display = "block";
      imageFilesError.textContent = "Number detail image must be <= 6";
    } else {
      for (var i = 0; i < files.length; i++) {
        if (!validFileTypes.includes(files[i].type)) {
          isValid = false;
          imageFilesError.style.display = "block";
          imageFilesError.textContent =
            "File valid with extension.jpg/.jpeg/.png.";
          break;
        } else if (files[i].size > maxSize) {
          isValid = false;
          imageFilesError.style.display = "block";
          imageFilesError.textContent = `File ${files[i].name} is too large! Maximum size is 10MB`;
          break;
        } else {
          imageFilesError.style.display = "none";
        }
      }
    }

    // Prevent form submission if any validation fails
    if (isValid) {
      form.submit();
    }
  });
});
