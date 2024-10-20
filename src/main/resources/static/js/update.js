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

    const maxSize = 10 * 1024 * 1024; // 10MB for file size



    document.getElementById("btnUpload").addEventListener("click", () => {
        document.getElementById("imageFiles").click();
    });

    imageFilesInput.addEventListener("change", function (e) {
        var files = e.target.files;

        const newFiles = Array.form(...files);
        if (newFiles.length > 6) {
            isValid = false;
            imageFilesError.style.display = "block";
            imageFilesError.textContent = "Number detail images must be <= 6";
            e.target.value = "";
            return;
        } else {
            for (var i = 0; i < newFiles.length; i++) {
                var file = newFiles[i];
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

        }
    });

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
        } else if (name !== initialNameValue) {
            try {
                const response = await fetch(
                    `http://localhost:9898/products/check-name?name=${encodeURIComponent(name)}`
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
                nameError.textContent = "Error checking product name. Please try again.";
                nameError.style.display = "block";
            }
        } else {
            nameError.style.display = "none";
        }

        const price = priceInput.value;
        if (price < 0) {
            isValid = false;
            priceError.textContent = "Price must be greater than or equal to zero";
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
            desError.textContent = "The description must be between 1 and 2000 characters.";
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

        // Validate color field
        let colorSelected = false;
        for (const colorSelect of colorSelects) {
            if (colorSelect.checked) {
                colorSelected = true;
                break;
            }
        }

        if (isValid) {
           form.submit();
        }
    });
});