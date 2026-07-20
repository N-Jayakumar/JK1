/**
 * @module formatter
 * @description Data formatting utilities (Currency, Dates, etc.).
 */
"use strict";

import { CONFIG } from '../config/config.js';

export const formatCurrency = (amount) => {
    return new Intl.NumberFormat(CONFIG.LANGUAGE, {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
};

export const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(CONFIG.LANGUAGE, options);
};
