document.addEventListener("DOMContentLoaded", function () {


//    function formatDateInput(input) {
//        if (input.value) {
//            const [year, month, day] = input.value.split("-");
//            input.value = `${year}/${month}/${day}`;
//        }
//    }
//
//    function unformatDateInput(input) {
//        if (input.value) {
//            const [year, month, day] = input.value.split("/");
//            input.value = `${year}-${month}-${day}`;
//        }
//    }
        let productId;
        document.querySelectorAll('.btn-delete').forEach(button => {
                button.addEventListener('click', function () {
                    productId = parseInt(this.getAttribute('data-id'));
                    console.log(productId);
                });
            });
        document.getElementById('btnConfirmDelete').addEventListener('click', async function () {
            try {
            const response = await fetch(`http://192.84.103.230:9898/products/${productId}/confirm-delete`);
            if (!response.ok) throw new Error('Network response was not ok');
            const result = await response.json();
            if (result.message === 'Delete Successfully') {
            location.reload();
            } else {
            alert('Xóa không thành công');
            }

            }catch(error) {
            console.error('Error delete:', error);
            }
        });


});
