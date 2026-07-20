$layout = @"
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:replace="~{::title}">JK1 Premium E-Commerce</title>
    
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <!-- AOS Animation -->
    <link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
    
    <!-- Custom CSS -->
    <link rel="stylesheet" th:href="@{/css/base/variables.css}">
    <link rel="stylesheet" th:href="@{/css/base/global.css}">
    
    <th:block th:replace="~{::styles}"></th:block>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
    
    <!-- Loading Overlay -->
    <div th:replace="~{components/loading :: loading}"></div>

    <!-- Header / Navbar -->
    <header th:replace="~{fragments/header :: navbar}"></header>

    <!-- Main Content -->
    <main class="flex-grow-1" id="main-content">
        <div th:replace="~{fragments/alert :: alerts}"></div>
        
        <!-- Inject Page Content -->
        <div th:replace="~{::content}"></div>
    </main>

    <!-- Footer -->
    <footer th:replace="~{fragments/footer :: footer}"></footer>

    <!-- Bootstrap 5.3 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Alpine.js -->
    <script defer src="https://cdn.jsdelivr.net/npm/alpinejs@3.13.3/dist/cdn.min.js"></script>
    
    <!-- AOS Animation JS -->
    <script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>
    
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    
    <!-- Custom JS -->
    <script th:src="@{/js/utils/alpine-config.js}"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            AOS.init({ once: true, offset: 50 });
        });
    </script>
    
    <th:block th:replace="~{::scripts}"></th:block>
</body>
</html>
"@
Set-Content -Path "src/main/resources/templates/layouts/main.html" -Value $layout

$header = @"
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <header th:fragment="navbar" class="bg-white shadow-sm sticky-top" x-data="{ mobileMenuOpen: false }">
        <nav class="navbar navbar-expand-lg navbar-light py-3">
            <div class="container">
                <a class="navbar-brand fw-bold fs-3 text-primary font-poppins" th:href="@{/}">JK1<span class="text-dark">.</span></a>
                
                <button class="navbar-toggler border-0 d-lg-none" type="button" @click="mobileMenuOpen = !mobileMenuOpen">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse d-none d-lg-flex" id="navbarNav">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-4 fw-medium">
                        <li class="nav-item"><a class="nav-link active" th:href="@{/}">Home</a></li>
                        <li class="nav-item"><a class="nav-link" th:href="@{/categories}">Categories</a></li>
                        <li class="nav-item"><a class="nav-link" th:href="@{/deals}">Deals</a></li>
                    </ul>
                    
                    <form class="d-flex mx-auto position-relative" style="max-width: 400px; width: 100%;">
                        <input class="form-control rounded-pill pe-5 bg-light border-0" type="search" placeholder="Search products..." aria-label="Search">
                        <button class="btn position-absolute end-0 top-50 translate-middle-y text-muted border-0" type="submit">
                            <i class="bi bi-search"></i>
                        </button>
                    </form>
                    
                    <ul class="navbar-nav ms-auto align-items-center gap-3">
                        <li class="nav-item">
                            <a class="nav-link position-relative" th:href="@{/wishlist}">
                                <i class="bi bi-heart fs-5"></i>
                                <span class="position-absolute top-25 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.65rem;">0</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link position-relative" th:href="@{/cart}">
                                <i class="bi bi-cart3 fs-5"></i>
                                <span class="position-absolute top-25 start-100 translate-middle badge rounded-pill bg-primary" style="font-size: 0.65rem;">0</span>
                            </a>
                        </li>
                        <li class="nav-item ms-2">
                            <a class="btn btn-outline-primary rounded-pill px-4" th:href="@{/login}">Login</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        
        <!-- Mobile Menu (Alpine.js) -->
        <div class="bg-white px-3 py-4 shadow-lg position-absolute w-100 z-3" x-show="mobileMenuOpen" x-transition.opacity style="display: none;">
            <ul class="list-unstyled mb-0 d-flex flex-column gap-3">
                <li><a class="text-decoration-none text-dark fw-medium" th:href="@{/}">Home</a></li>
                <li><a class="text-decoration-none text-dark fw-medium" th:href="@{/categories}">Categories</a></li>
                <li><a class="text-decoration-none text-dark fw-medium" th:href="@{/cart}">Cart</a></li>
                <li><hr class="my-1"></li>
                <li><a class="btn btn-primary w-100 rounded-pill" th:href="@{/login}">Login / Register</a></li>
            </ul>
        </div>
    </header>
</body>
</html>
"@
Set-Content -Path "src/main/resources/templates/fragments/header.html" -Value $header

$footer = @"
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <footer th:fragment="footer" class="bg-white pt-5 pb-3 mt-5 border-top">
        <div class="container">
            <div class="row gy-4">
                <div class="col-lg-4 col-md-6">
                    <h5 class="fw-bold font-poppins text-primary mb-3">JK1.</h5>
                    <p class="text-muted text-sm">Premium eCommerce experience offering high-quality products with fast delivery and exceptional customer support.</p>
                    <div class="d-flex gap-3 mt-4">
                        <a href="#" class="text-muted text-decoration-none fs-5 hover-primary"><i class="bi bi-facebook"></i></a>
                        <a href="#" class="text-muted text-decoration-none fs-5 hover-primary"><i class="bi bi-twitter-x"></i></a>
                        <a href="#" class="text-muted text-decoration-none fs-5 hover-primary"><i class="bi bi-instagram"></i></a>
                    </div>
                </div>
                <div class="col-lg-2 col-md-6">
                    <h6 class="fw-bold mb-3">Quick Links</h6>
                    <ul class="list-unstyled text-muted d-flex flex-column gap-2 text-sm">
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">Home</a></li>
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">Shop</a></li>
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">About Us</a></li>
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">Contact</a></li>
                    </ul>
                </div>
                <div class="col-lg-3 col-md-6">
                    <h6 class="fw-bold mb-3">Customer Support</h6>
                    <ul class="list-unstyled text-muted d-flex flex-column gap-2 text-sm">
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">FAQ</a></li>
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">Shipping Policy</a></li>
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">Returns</a></li>
                        <li><a href="#" class="text-decoration-none text-muted hover-primary">Track Order</a></li>
                    </ul>
                </div>
                <div class="col-lg-3 col-md-6">
                    <h6 class="fw-bold mb-3">Newsletter</h6>
                    <p class="text-muted text-sm mb-3">Subscribe to get special offers and updates.</p>
                    <form class="d-flex gap-2">
                        <input type="email" class="form-control text-sm bg-light border-0" placeholder="Your Email">
                        <button class="btn btn-primary text-sm px-3" type="button">Subscribe</button>
                    </form>
                </div>
            </div>
            <hr class="my-4 text-muted">
            <div class="text-center text-muted text-sm">
                &copy; 2026 JK1 eCommerce. All rights reserved.
            </div>
        </div>
    </footer>
</body>
</html>
"@
Set-Content -Path "src/main/resources/templates/fragments/footer.html" -Value $footer

$alert = @"
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="alerts" class="container mt-3">
        <div th:if="`${successMessage}`" class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert" data-aos="fade-down">
            <i class="bi bi-check-circle-fill me-2"></i>
            <span th:text="`${successMessage}`">Success!</span>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <div th:if="`${errorMessage}`" class="alert alert-danger alert-dismissible fade show border-0 shadow-sm" role="alert" data-aos="fade-down">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <span th:text="`${errorMessage}`">Error!</span>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
</body>
</html>
"@
$alert = $alert -replace "`${", "`$${" # Fix escaping for thymeleaf
Set-Content -Path "src/main/resources/templates/fragments/alert.html" -Value $alert

$loading = @"
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="loading" class="d-none position-fixed top-0 start-0 w-100 h-100 bg-white bg-opacity-75 z-index-master d-flex justify-content-center align-items-center" id="globalLoader">
        <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>
</body>
</html>
"@
Set-Content -Path "src/main/resources/templates/components/loading.html" -Value $loading

$sidebar = @"
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="sidebar" class="bg-white p-3 border rounded shadow-sm">
        <h5 class="fw-bold mb-3">Dashboard</h5>
        <ul class="nav flex-column">
            <li class="nav-item mb-2"><a href="#" class="nav-link text-dark hover-primary px-0">Profile</a></li>
            <li class="nav-item mb-2"><a href="#" class="nav-link text-dark hover-primary px-0">Orders</a></li>
            <li class="nav-item mb-2"><a href="#" class="nav-link text-dark hover-primary px-0">Settings</a></li>
        </ul>
    </div>
</body>
</html>
"@
Set-Content -Path "src/main/resources/templates/fragments/sidebar.html" -Value $sidebar

$breadcrumb = @"
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <nav th:fragment="breadcrumb(pageName)" aria-label="breadcrumb" class="py-3">
        <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item"><a th:href="@{/}" class="text-decoration-none">Home</a></li>
            <li class="breadcrumb-item active" aria-current="page" th:text="`${pageName}`">Page</li>
        </ol>
    </nav>
</body>
</html>
"@
$breadcrumb = $breadcrumb -replace "`${", "`$${"
Set-Content -Path "src/main/resources/templates/fragments/breadcrumb.html" -Value $breadcrumb

$modal = @"
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="modal(id, title, contentFragment)" class="modal fade" th:id="`${id}`" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold" th:text="`${title}`">Modal Title</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" th:replace="`${contentFragment}`">
                    Modal Content Placeholder
                </div>
            </div>
        </div>
    </div>
</body>
</html>
"@
$modal = $modal -replace "`${", "`$${"
Set-Content -Path "src/main/resources/templates/components/modal.html" -Value $modal

$home = @"
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" th:replace="~{layouts/main :: layout(~{::title}, ~{::content}, ~{}, ~{})}">
<head>
    <title>Home - JK1</title>
</head>
<body>
    <div th:fragment="content">
        <!-- Hero Section Placeholder -->
        <section class="bg-primary text-white py-5 mb-5 rounded shadow-sm mx-3 mt-3" data-aos="fade-up">
            <div class="container py-5 text-center">
                <h1 class="display-4 fw-bold font-poppins mb-4">Welcome to JK1 Premium</h1>
                <p class="lead mb-5 opacity-75">Your ultimate eCommerce destination.</p>
                <a href="#" class="btn btn-light btn-lg rounded-pill px-5 fw-medium text-primary shadow">Shop Now</a>
            </div>
        </section>
        
        <div class="container my-5">
            <div class="text-center mb-5" data-aos="fade-up">
                <h2 class="fw-bold font-poppins">Featured Products</h2>
                <p class="text-muted">Placeholder for dynamic products</p>
            </div>
        </div>
    </div>
</body>
</html>
"@
Set-Content -Path "src/main/resources/templates/home/index.html" -Value $home

$variablesCss = @"
:root {
    --jk1-primary: #0d6efd;
    --jk1-secondary: #6c757d;
    --jk1-success: #198754;
    --jk1-danger: #dc3545;
    --jk1-warning: #ffc107;
    --jk1-info: #0dcaf0;
    --jk1-light: #f8f9fa;
    --jk1-dark: #212529;
    --jk1-font-sans: 'Inter', sans-serif;
    --jk1-font-display: 'Poppins', sans-serif;
}
"@
Set-Content -Path "src/main/resources/static/css/base/variables.css" -Value $variablesCss

$globalCss = @"
body {
    font-family: var(--jk1-font-sans);
    color: var(--jk1-dark);
}
.font-poppins {
    font-family: var(--jk1-font-display);
}
.hover-primary:hover {
    color: var(--jk1-primary) !important;
    transition: color 0.3s ease;
}
.z-index-master {
    z-index: 9999;
}
.text-sm {
    font-size: 0.875rem;
}
"@
Set-Content -Path "src/main/resources/static/css/base/global.css" -Value $globalCss

$alpineJs = @"
document.addEventListener('alpine:init', () => {
    Alpine.data('jk1Store', () => ({
        init() {
            console.log('Alpine.js Initialized for JK1 eCommerce');
        }
    }));
});
"@
Set-Content -Path "src/main/resources/static/js/utils/alpine-config.js" -Value $alpineJs

Write-Host "UI fragments, layouts, and assets generated."
