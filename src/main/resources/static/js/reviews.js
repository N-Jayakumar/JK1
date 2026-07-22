document.addEventListener('DOMContentLoaded', () => {
    // Star Rating Selection in Modal
    const stars = document.querySelectorAll('#modal-star-rating i');
    const ratingInput = document.getElementById('review-rating-val');
    
    stars.forEach(star => {
        star.addEventListener('click', (e) => {
            const val = parseInt(e.target.getAttribute('data-val'));
            ratingInput.value = val;
            stars.forEach(s => {
                const sVal = parseInt(s.getAttribute('data-val'));
                if (sVal <= val) {
                    s.classList.replace('bi-star', 'bi-star-fill');
                } else {
                    s.classList.replace('bi-star-fill', 'bi-star');
                }
            });
        });
    });

    // Handle Review Form Submission
    const reviewForm = document.getElementById('writeReviewForm');
    if (reviewForm) {
        reviewForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(reviewForm);
            
            // Assuming CSRF is handled via meta tags in main layout
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            try {
                const response = await fetch(reviewForm.action, {
                    method: 'POST',
                    headers: headers,
                    body: formData
                });
                
                if (response.ok) {
                    // Close modal and reload page
                    const modal = bootstrap.Modal.getInstance(document.getElementById('writeReviewModal'));
                    if (modal) modal.hide();
                    window.location.reload();
                } else {
                    const data = await response.json();
                    alert(data.error || 'Failed to submit review');
                }
            } catch (err) {
                console.error(err);
                alert('An error occurred while submitting your review.');
            }
        });
    }

    // Handle Helpful Votes
    document.querySelectorAll('.vote-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const id = btn.getAttribute('data-id');
            const isHelpful = btn.getAttribute('data-helpful') === 'true';
            
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            try {
                const response = await fetch(`/api/reviews/${id}/vote`, {
                    method: 'POST',
                    headers: headers,
                    body: new URLSearchParams({ helpful: isHelpful })
                });
                
                if (response.ok) {
                    window.location.reload(); // Simple reload to reflect updated count
                } else {
                    alert('You must be logged in to vote.');
                }
            } catch (err) {
                console.error(err);
            }
        });
    });
    
    // Handle QA Submission
    const qaForm = document.getElementById('askQuestionForm');
    if (qaForm) {
        qaForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(qaForm);
            
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            try {
                const response = await fetch(qaForm.action, {
                    method: 'POST',
                    headers: headers,
                    body: formData
                });
                
                if (response.ok) {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('askQuestionModal'));
                    if (modal) modal.hide();
                    window.location.reload();
                } else {
                    const data = await response.json();
                    alert(data.error || 'Failed to submit question');
                }
            } catch (err) {
                console.error(err);
            }
        });
    }
});
