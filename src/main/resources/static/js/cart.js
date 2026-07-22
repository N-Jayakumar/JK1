"use strict";

document.addEventListener("DOMContentLoaded", () => {
    // Utility to get CSRF token from cookie (Spring Security CookieCsrfTokenRepository)
    const getCsrfToken = () => {
        let matches = document.cookie.match(new RegExp("(?:^|; )XSRF-TOKEN=([^;]*)"));
        return matches ? decodeURIComponent(matches[1]) : '';
    };
    const getCsrfHeader = () => {
        return 'X-XSRF-TOKEN';
    };

    window.updateQuantity = function(itemId, change) {
        const qtySpan = document.getElementById('qty-' + itemId);
        let currentQty = parseInt(qtySpan.innerText);
        let newQty = currentQty + change;

        if (newQty < 1) {
            // If they try to reduce below 1, let the remove form handle it, or just return
            if (confirm("Remove item from cart?")) {
                newQty = 0;
            } else {
                return;
            }
        }

        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();
        const headers = {
            'Content-Type': 'application/x-www-form-urlencoded'
        };
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        fetch(`/cart/api/update/${itemId}?quantity=${newQty}`, {
            method: 'POST',
            headers: headers
        })
        .then(response => response.json().then(data => ({ status: response.status, body: data })))
        .then(result => {
            if (result.status === 200 && result.body.success) {
                if (newQty === 0) {
                    window.location.reload(); // Reload if item removed
                    return;
                }
                // Update UI instantly
                qtySpan.innerText = newQty;
                
                // Update item total
                const itemTotalEl = document.getElementById('item-total-' + itemId);
                if (itemTotalEl) {
                    itemTotalEl.innerText = '₹' + parseFloat(result.body.itemTotal).toFixed(2);
                }

                // Update summary
                const totalItemsEl = document.getElementById('summary-total-items');
                if (totalItemsEl) totalItemsEl.innerText = result.body.totalItems;

                const subtotalEl = document.getElementById('summary-subtotal');
                if (subtotalEl) subtotalEl.innerText = '₹' + parseFloat(result.body.subtotal).toFixed(2);

                const discountEl = document.getElementById('summary-discount');
                if (discountEl && result.body.discount > 0) {
                    discountEl.innerText = '-₹' + parseFloat(result.body.discount).toFixed(2);
                    discountEl.parentElement.classList.remove('d-none');
                } else if (discountEl) {
                    discountEl.parentElement.classList.add('d-none');
                }

                const grandTotalEl = document.getElementById('summary-grand-total');
                if (grandTotalEl) grandTotalEl.innerText = '₹' + parseFloat(result.body.cartTotal).toFixed(2);
                
                const cartHeaderCountEl = document.querySelector('.cart-header-count');
                if (cartHeaderCountEl) cartHeaderCountEl.innerText = result.body.totalItems + ' item(s) in your cart';

            } else {
                alert(result.body.error || 'Failed to update quantity');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while updating the cart.');
        });
    };
});
