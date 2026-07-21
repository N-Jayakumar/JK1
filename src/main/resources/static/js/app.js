/**
 * @module app
 * @description Main entry point for the JKØ Premium Men's Formal Fashion Brand application.
 * Initializes global configurations, utilities, and generic components.
 */
"use strict";


document.addEventListener("DOMContentLoaded", () => {
    console.log("JKØ App Initialized");
    
    // Initialize AOS Animation
    if (typeof AOS !== 'undefined') {
        AOS.init({ once: true, offset: 50 });
    }

    // Global event delegation for confirmation dialogues
    document.addEventListener('click', (e) => {
        const confirmBtn = e.target.closest('[data-confirm]');
        if (confirmBtn) {
            const message = confirmBtn.getAttribute('data-confirm') || 'Are you sure?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        }
    });

    // Bootstrap Form Validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
});
