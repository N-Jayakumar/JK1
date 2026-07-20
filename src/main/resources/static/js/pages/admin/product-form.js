"use strict";

/**
 * @module pages/admin/product-form
 * @description Page specific logic for admin\product-form.html.
 */

document.addEventListener("alpine:init", () => {
    Alpine.data("productForm", () => ({
        selectedCategory: '',
        requiredAttributes: [],
        attributesData: {},
        
        init() {
            // Read data from attribute
            const container = document.querySelector('[x-data="productForm"]');
            if (container && container.dataset.attributes) {
                try {
                    this.attributesData = JSON.parse(container.dataset.attributes);
                } catch (e) {
                    console.error("Failed to parse attributesData", e);
                }
            }

            // Pre-fill if editing
            const categorySelect = document.querySelector('select[name="categoryId"]');
            if (categorySelect && categorySelect.selectedIndex > 0) {
                this.updateAttributes(categorySelect.options[categorySelect.selectedIndex].text);
            }
        },
        
        updateAttributes(categoryName) {
            this.selectedCategory = categoryName.toLowerCase();
            if (this.selectedCategory.includes('shirt')) {
                this.requiredAttributes = ['Size', 'Color', 'Fit', 'Sleeve', 'Fabric'];
            } else if (this.selectedCategory.includes('pant')) {
                this.requiredAttributes = ['Waist', 'Length', 'Fit', 'Color'];
            } else if (this.selectedCategory.includes('shoe')) {
                this.requiredAttributes = ['Size', 'Color', 'Material'];
            } else if (this.selectedCategory.includes('belt')) {
                this.requiredAttributes = ['Length', 'Color', 'Material'];
            } else if (this.selectedCategory.includes('watch')) {
                this.requiredAttributes = ['Dial', 'Strap', 'Movement'];
            } else if (this.selectedCategory.includes('bracelet')) {
                this.requiredAttributes = ['Material', 'Size'];
            } else if (this.selectedCategory.includes('perfume')) {
                this.requiredAttributes = ['Volume', 'Fragrance'];
            } else {
                this.requiredAttributes = [];
            }
        }
    }));
});

document.addEventListener("DOMContentLoaded", () => {
    // TODO: Initialize page specific features
});
