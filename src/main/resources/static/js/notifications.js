document.addEventListener('DOMContentLoaded', () => {
    const badge = document.getElementById('unread-notification-count');
    if (badge) {
        fetchUnreadCount();
        
        // Optional: Poll every minute
        setInterval(fetchUnreadCount, 60000);
    }

    async function fetchUnreadCount() {
        try {
            const response = await fetch('/user/notifications/unread-count');
            if (response.ok) {
                const count = await response.json();
                if (count > 0) {
                    badge.textContent = count;
                    badge.classList.remove('d-none');
                } else {
                    badge.classList.add('d-none');
                }
            }
        } catch (error) {
            console.error('Error fetching unread count:', error);
        }
    }
});
