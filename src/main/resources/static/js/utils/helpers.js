/**
 * @module helpers
 * @description General utility helper functions for JKØ.
 */
"use strict";

/**
 * Creates an HTML element with classes and attributes.
 * @param {string} tag 
 * @param {Array} classes 
 * @param {Object} attributes 
 * @returns {HTMLElement}
 */
export const createElement = (tag, classes = [], attributes = {}) => {
    const el = document.createElement(tag);
    if (classes.length) el.classList.add(...classes);
    for (const [key, value] of Object.entries(attributes)) {
        el.setAttribute(key, value);
    }
    return el;
};

// TODO: Add more generic helper functions as needed
