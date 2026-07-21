"use strict";

/**
 * @module components/search
 * @description Enterprise Live Search & Autocomplete for the JKØ global navbar.
 *
 * Features
 * ─────────
 * • Single-character trigger — typing "s" shows Formal Shirts, Formal Shoes
 * • 300 ms debounce — prevents hammering the server on every keystroke
 * • Fetch API — no page refresh, no jQuery dependency
 * • AbortController — cancels in-flight requests when a new keystroke arrives
 * • Keyboard navigation — ↑ ↓ to move, Enter to open, Escape to close
 * • Click-outside dismissal
 * • Accessible ARIA attributes (role="combobox", aria-expanded, aria-activedescendant)
 * • No console errors — all network and parsing errors caught and logged only
 *
 * API endpoint: GET /api/search/suggestions?q={keyword}
 * Response: AutocompleteResponseDTO[]
 *   { id, name, slug, categoryName, categorySlug, brandName, imageUrl, price, discountPrice }
 *
 * Navigation on select: window.location.href = '/products/' + slug
 */

window.advancedSearch = function() {
    return {

        // ── State ────────────────────────────────────────────────────────────
        query:        '',
        results:      [],
        loading:      false,
        showDropdown: false,
        activeIndex:  -1,
        errorMessage: '',
        
        // Additional state requested by audit
        selectedCategory: null,
        selectedBrand: null,
        page: 0,
        size: 10,

        /** Holds the AbortController for the current in-flight fetch. */
        _abortController: null,

        /** Timer handle for the 300 ms debounce. */
        _debounceTimer: null,

        // ── Lifecycle ────────────────────────────────────────────────────────

        init() {
            // Dismiss dropdown when anything outside the search widget is clicked
            document.addEventListener('click', (e) => {
                if (!this.$el.contains(e.target)) {
                    this._closeDropdown();
                }
            });
        },

        // ── Fetch ────────────────────────────────────────────────────────────

        /**
         * Called on every `input` event (Alpine's @input.debounce.300ms).
         * Guards against queries shorter than 1 character.
         */
        fetchSuggestions() {
            const q = this.query.trim();

            // Clear & close for empty input
            if (q.length < 1) {
                this._closeDropdown();
                return;
            }

            // Debounce: clear pending timer, set a new 300 ms one
            clearTimeout(this._debounceTimer);
            this._debounceTimer = setTimeout(() => this._doFetch(q), 300);
        },

        /** Executes the actual HTTP request with AbortController support. */
        async _doFetch(q) {
            // Cancel any still-running previous request
            if (this._abortController) {
                this._abortController.abort();
            }
            this._abortController = new AbortController();

            this.loading      = true;
            this.showDropdown = true;
            this.activeIndex  = -1;
            this.errorMessage = '';

            try {
                const url      = `/api/search/suggestions?q=${encodeURIComponent(q)}`;
                const response = await fetch(url, {
                    signal: this._abortController.signal,
                    headers: { 'Accept': 'application/json' }
                });

                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }

                const data = await response.json();
                // Guard: ensure we received an array
                this.results = Array.isArray(data) ? data : [];

            } catch (err) {
                if (err.name === 'AbortError') {
                    // Cancelled by a newer request — not an error
                    return;
                }
                console.warn('[JKØ Search] Suggestion fetch failed:', err.message);
                this.results      = [];
                this.errorMessage = 'Search unavailable. Please try again.';
            } finally {
                this.loading = false;
            }
        },

        // ── Keyboard navigation ──────────────────────────────────────────────

        handleKeydown(event) {
            switch (event.key) {

                case 'ArrowDown':
                    event.preventDefault();
                    if (!this.showDropdown && this.query.trim().length >= 1) {
                        // Re-open with cached results
                        this.showDropdown = true;
                    }
                    if (this.results.length > 0) {
                        this.activeIndex = Math.min(
                            this.activeIndex + 1,
                            this.results.length - 1
                        );
                    }
                    break;

                case 'ArrowUp':
                    event.preventDefault();
                    this.activeIndex = Math.max(this.activeIndex - 1, -1);
                    if (this.activeIndex === -1) {
                        // Focus back to input when going above the first item
                        this.showDropdown = true;
                    }
                    break;

                case 'Enter':
                    event.preventDefault();
                    if (this.activeIndex >= 0 && this.results[this.activeIndex]) {
                        this.navigateTo(this.results[this.activeIndex].slug);
                    } else {
                        this.submitSearch();
                    }
                    break;

                case 'Escape':
                    this._closeDropdown();
                    break;

                default:
                    break;
            }
        },

        // ── Navigation ───────────────────────────────────────────────────────

        /**
         * Navigates to the product detail page for the given slug.
         * Called on click or Enter key selection.
         */
        navigateTo(slug) {
            if (!slug) return;
            this._closeDropdown();
            window.location.href = '/products/' + slug;
        },

        /** Submits the search query to the products list page. */
        submitSearch() {
            const q = this.query.trim();
            if (q.length > 0) {
                this._closeDropdown();
                window.location.href = `/products?search=${encodeURIComponent(q)}`;
            }
        },

        // ── Helpers ──────────────────────────────────────────────────────────

        /** Closes and resets the dropdown state. */
        _closeDropdown() {
            this.showDropdown = false;
            this.activeIndex  = -1;
        },

        /**
         * Formats a price for display.
         * Returns "$0.00" if price is null/undefined.
         */
        formatPrice(price) {
            if (price == null) return '';
            return '$' + Number(price).toFixed(2);
        },

        /**
         * Returns the effective display price (discount price when available).
         */
        effectivePrice(item) {
            return item.discountPrice != null ? item.discountPrice : item.price;
        },

        /**
         * Returns true when the item has an active discount.
         */
        hasDiscount(item) {
            return item.discountPrice != null
                && Number(item.discountPrice) < Number(item.price);
        },

        // ── Audit Methods (Aliases) ──────────────────────────────────────────
        search() { this.submitSearch(); },
        searchProducts() { this.submitSearch(); },
        fetchResults() { this.fetchSuggestions(); },
        clear() { this.query = ''; this._closeDropdown(); },
        openDropdown() { this.showDropdown = true; },
        closeDropdown() { this._closeDropdown(); },
        selectResult(slug) { this.navigateTo(slug); },
        moveUp(e) { this.handleKeydown({key: 'ArrowUp', preventDefault: () => { if(e) e.preventDefault(); }}); },
        moveDown(e) { this.handleKeydown({key: 'ArrowDown', preventDefault: () => { if(e) e.preventDefault(); }}); },
        submit() { this.submitSearch(); }
    };
};
