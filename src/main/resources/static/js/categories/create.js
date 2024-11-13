document.addEventListener("DOMContentLoaded", function () {
    let isValid = true;
    const form = document.querySelector(".formCreateCategory");
    const nameCategoryInput = document.getElementById("nameCategoryInput");
    const nameCategoryError = document.getElementById("nameCategoryError");

    form.addEventListener("submit", async function (event) {
        isValid = true;
        event.preventDefault();
        const nameCategory = nameCategoryInput.value;
        if(!nameCategory) {
             isValid = false;
             nameCategoryError.textContent = "The category name is required.";
             nameCategoryError.style.display = "block";
        }else if(nameCategory.length > 25) {
            isValid = false;
            nameCategoryError.textContent = "The category name must be between 1 and 25 characters.";
            nameCategoryError.style.display = "block";
        }else {
            try {
                    const response = await fetch(
                      `http://192.168.1.221:9898/categories/check-name?name=${encodeURIComponent(
                        nameCategory
                      )}`
                    );
                    const exist = await response.json();
                    if (exist) {
                      isValid = false;
                      nameCategoryError.textContent = "The category name already exist";
                      nameCategoryError.style.display = "block";
                    } else {
                      nameCategoryError.style.display = "none";
                    }
                  } catch (error) {
                    isValid = false;
                    nameCategoryError.textContent = "Error checking product name. Please try again";
                    nameCategoryError.style.display = "block";
                  }
        }



    });


})