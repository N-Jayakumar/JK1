/**
 * @module debounce
 * @description Utility for debouncing frequent function calls (e.g. search inputs).
 */
"use strict";

export const debounce = (func, wait) => {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
};
