package com.jk1.config;

import com.jk1.entity.*;
import com.jk1.entity.enums.ProductStatus;
import com.jk1.repository.BrandRepository;
import com.jk1.repository.CategoryRepository;
import com.jk1.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Seeds initial product catalog for JKØ if the database is empty.
 * Ensures the storefront is not empty on first launch.
 */
@Slf4j
@Component
@Order(2) // Run after DataInitializer (which seeds Roles and Admin)
@RequiredArgsConstructor
public class ProductDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("[ProductDataSeeder] Products already exist. Skipping seed.");
            return;
        }

        log.info("[ProductDataSeeder] Products table is empty. Seeding catalog...");

        // 1. Create Brand
        Brand brand = brandRepository.findByName("JKØ").orElseGet(() -> {
            Brand newBrand = Brand.builder()
                    .name("JKØ")
                    .description("Premium fashion and lifestyle")
                    .build();
            return brandRepository.save(newBrand);
        });

        // 2. Define Categories and Seed Products
        String[] categories = {
                "Formal Shirts", "Formal Pants", "Formal Shoes",
                "Leather Belts", "Watches", "Bracelets", "Perfumes"
        };

        int totalSeeded = 0;

        for (String catName : categories) {
            Category category = getOrCreateCategory(catName);

            for (int i = 1; i <= 10; i++) {
                createProduct(category, brand, i);
                totalSeeded++;
            }
        }

        log.info("[ProductDataSeeder] Successfully seeded {} products.", totalSeeded);
    }

    private Category getOrCreateCategory(String name) {
        String slug = name.toLowerCase().replace(" ", "-");
        // We use findAll() and filter manually since there's no guaranteed findByName method in the interface
        // that we know of, though we could write a custom query. Filtering in memory is fine for 7 categories.
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Category cat = Category.builder()
                            .name(name)
                            .slug(slug)
                            .description("Premium " + name)
                            .build();
                    return categoryRepository.save(cat);
                });
    }

    private void createProduct(Category category, Brand brand, int index) {
        String baseName = getBaseNameForCategory(category.getName());
        String name = baseName + (index == 1 ? "" : " Edition " + index);
        
        // Ensure uniqueness even if run multiple times
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String slug = name.toLowerCase().replace(" ", "-") + "-" + uuid;
        String sku = category.getName().substring(0, 2).toUpperCase() + "-" + String.format("%04d", index) + "-" + uuid;

        Product product = Product.builder()
                .name(name)
                .slug(slug)
                .sku(sku)
                .description("Experience the pinnacle of luxury with this " + name.toLowerCase() + ". Designed for modern elegance and crafted with precision.")
                .price(BigDecimal.valueOf(100.0 + (index * 25.5)))
                .productStatus(ProductStatus.ACTIVE)
                .category(category)
                .brand(brand)
                .build();

        // Inventory
        Inventory inventory = Inventory.builder()
                .quantity(50 + index * 5)
                .reservedQuantity(0)
                .product(product)
                .build();
        product.setInventory(inventory);

        // Image (Placeholder based on category)
        ProductImage image = ProductImage.builder()
                .imageUrl(getImageForCategory(category.getName()))
                .isPrimary(true)
                .altText(name)
                .product(product)
                .build();
        product.getImages().add(image);

        // Attributes (Featured flag)
        if (index <= 2) {
            product.getAttributes().put("featured", "true");
        }

        productRepository.save(product);
    }

    private String getBaseNameForCategory(String categoryName) {
        return switch (categoryName) {
            case "Formal Shirts" -> "Executive White Shirt";
            case "Formal Pants" -> "Slim Fit Navy Trouser";
            case "Formal Shoes" -> "Oxford Leather Shoe";
            case "Leather Belts" -> "Premium Black Leather Belt";
            case "Watches" -> "Executive Steel Watch";
            case "Bracelets" -> "Premium Leather Bracelet";
            case "Perfumes" -> "Midnight Signature Perfume";
            default -> "Premium " + categoryName + " Item";
        };
    }

    private String getImageForCategory(String categoryName) {
        return switch (categoryName) {
            case "Formal Shirts" -> "https://images.unsplash.com/photo-1602810318383-e386cc2a3ceb?q=80&w=800&auto=format&fit=crop";
            case "Formal Pants" -> "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?q=80&w=800&auto=format&fit=crop";
            case "Formal Shoes" -> "https://images.unsplash.com/photo-1614252339475-531eba835eb1?q=80&w=800&auto=format&fit=crop";
            case "Leather Belts" -> "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=800&auto=format&fit=crop";
            case "Watches" -> "https://images.unsplash.com/photo-1524592094714-0f0654e20314?q=80&w=800&auto=format&fit=crop";
            case "Bracelets" -> "https://images.unsplash.com/photo-1611591437281-460bfbe1220a?q=80&w=800&auto=format&fit=crop";
            case "Perfumes" -> "https://images.unsplash.com/photo-1594035910387-fea47794261f?q=80&w=800&auto=format&fit=crop";
            default -> "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=800&auto=format&fit=crop";
        };
    }
}
