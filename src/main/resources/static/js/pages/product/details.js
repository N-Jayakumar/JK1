"use strict";

import { getItem, setItem } from '../../utils/storage.js';

/**
 * @module pages/product/details
 * @description Page specific logic for product\details.html.
 */

document.addEventListener("alpine:init", () => {
    
    // Recently Viewed Component
    Alpine.data('recentlyViewed', () => ({
        viewedProducts: [],
        
        init() {
            this.loadRecentlyViewed();
            this.saveCurrentProduct();
        },
        
        loadRecentlyViewed() {
            const list = getItem('jk_recently_viewed') || [];
            // Filter out current product so it doesn't show in "recently viewed" while viewing it
            const currentSlug = window.location.pathname.split('/').pop();
            this.viewedProducts = list.filter(p => p.slug !== currentSlug);
        },
        
        saveCurrentProduct() {
            // Extract product data from the DOM or we could pass it via data attribute.
            // But doing it via Alpine x-data in details.html is cleaner.
            // For now, let's just grab basic details from the page.
            try {
                const name = document.querySelector('h1').innerText;
                const priceText = document.querySelector('h2.text-primary').innerText;
                const price = priceText.replace('$', '').trim();
                const img = document.querySelector('.product-gallery-main img').src;
                const slug = window.location.pathname.split('/').pop();
                
                const product = { id: slug, slug, name, price, imageUrl: img };
                
                let list = getItem('jk_recently_viewed') || [];
                // Remove if already exists
                list = list.filter(p => p.slug !== slug);
                // Add to front
                list.unshift(product);
                // Keep max 10
                if (list.length > 10) {
                    list = list.slice(0, 10);
                }
                
                setItem('jk_recently_viewed', list);
            } catch (e) {
                console.error("Failed to save recently viewed", e);
            }
        }
    }));
});

document.addEventListener("DOMContentLoaded", () => {
    // Image Zoom Effect (Vanilla JS)
    const galleryMain = document.querySelector('.product-gallery-main');
    const mainImg = galleryMain?.querySelector('img');
    
    if (galleryMain && mainImg) {
        galleryMain.addEventListener('mousemove', (e) => {
            const { left, top, width, height } = galleryMain.getBoundingClientRect();
            const x = (e.clientX - left) / width * 100;
            const y = (e.clientY - top) / height * 100;
            
            mainImg.style.transformOrigin = `${x}% ${y}%`;
            mainImg.style.transform = 'scale(2)';
            mainImg.style.cursor = 'zoom-in';
        });
        
        galleryMain.addEventListener('mouseleave', () => {
            mainImg.style.transformOrigin = 'center center';
            mainImg.style.transform = 'scale(1)';
        });
    }
});
