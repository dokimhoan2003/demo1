document.addEventListener("DOMContentLoaded", function () {

     document.querySelectorAll(".bi-calendar3").forEach((icon, index) => {
            icon.addEventListener("click", function() {
                const dateInput = index === 0 ? document.getElementById("fromCreateAt") : document.getElementById("toCreateAt");
                dateInput.focus();
            });
        });

    document.getElementById("fromCreateAt").addEventListener("change", function(e) {
        const date = new Date(e.target.value);
       if (e.target.value) {
               const formattedDate = date.toLocaleDateString('ja-JP', {
                   year: 'numeric',
                   month: '2-digit',
                   day: '2-digit'
               }).replace(/\//g, '-');
               document.getElementById("create_at_from").value = formattedDate;
           } else {
               document.getElementById("create_at_from").value = '';
           }
    });

    document.getElementById("toCreateAt").addEventListener("change", function(e) {
        const date = new Date(e.target.value);
        if(e.target.value) {
           const formattedDate = date.toLocaleDateString('ja-JP', {
                       year: 'numeric',
                       month: '2-digit',
                       day: '2-digit'
                   }).replace(/\//g, '-');
                   document.getElementById("create_at_to").value = formattedDate;
        }else {
            document.getElementById("create_at_to").value = '';
        }

    });



//    // Đặt lại khi trường văn bản từ chọn ngày 'fromCreateAt' trống
//    document.getElementById("create_at_from").addEventListener("input", function() {
//        if (this.value === "") {
//            document.getElementById("fromCreateAt").value = "";
//        }
//    });
//
//    // Đặt lại khi trường văn bản từ chọn ngày 'toCreateAt' trống
//    document.getElementById("create_at_to").addEventListener("input", function() {
//        if (this.value === "") {
//            document.getElementById("toCreateAt").value = "";
//        }
//    });

        let productId;
        document.querySelectorAll('.btn-delete').forEach(button => {
                button.addEventListener('click', function () {
                    productId = parseInt(this.getAttribute('data-id'));
                    console.log(productId);
                });
            });
        document.getElementById('btnConfirmDelete').addEventListener('click', async function () {
            try {
            const response = await fetch(`http://192.168.1.221:9898/products/${productId}/confirm-delete`);
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
