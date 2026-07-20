document.addEventListener('alpine:init', () => {
    Alpine.data('jk1Store', () => ({
        init() {
            console.log('Alpine.js Initialized for JK1 eCommerce');
        }
    }));
});
