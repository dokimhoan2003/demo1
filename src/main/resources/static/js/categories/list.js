document.addEventListener("DOMContentLoaded", function () {

        let categoryId;
        document.querySelectorAll('.btn-delete').forEach(button => {
                button.addEventListener('click', function () {
                    categoryId = parseInt(this.getAttribute('data-id'));
                });
            });

        document.getElementById('btnConfirmDelete').addEventListener('click', async function () {
            try {
            const response = await fetch(`http://192.168.1.221:9898/categories/${categoryId}/confirm-delete`);
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
