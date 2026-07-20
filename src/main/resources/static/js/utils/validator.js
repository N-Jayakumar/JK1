/**
 * @module validator
 * @description Common form and data validation utilities.
 */
"use strict";

export const isValidEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
};

export const isNotEmpty = (value) => {
    return value !== null && value !== undefined && String(value).trim() !== '';
};

export const isStrongPassword = (password) => {
    // At least 8 chars, 1 uppercase, 1 lowercase, 1 number
    const re = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
    return re.test(password);
};

export const isValidPhone = (phone) => {
    const re = /^\+?[1-9]\d{1,14}$/;
    return re.test(phone);
};
