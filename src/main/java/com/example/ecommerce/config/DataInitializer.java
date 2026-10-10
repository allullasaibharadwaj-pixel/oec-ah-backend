package com.example.ecommerce.config;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates demo data on first start: admin + demo customer, categories and products.
 * Passwords are stored BCrypt-encoded. Nothing is created twice (safe to restart).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, Category> categories = new HashMap<>();

    public DataInitializer(UserRepository userRepository, CategoryRepository categoryRepository,
                           ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUser("Shop Admin", "admin@shop.com", "Admin@123", "9999999999", "Admin Office, Main Street", Role.ADMIN);
        createUser("Demo Customer", "user@shop.com", "User@123", "8888888888", "12 Demo Street, Sample City", Role.USER);
        if (categoryRepository.count() == 0) {
        category("Electronics", "Laptops, phones, audio and smart gadgets");
        category("Fashion", "Clothing and footwear for every season");
        category("Home & Kitchen", "Appliances and essentials for your home");
        category("Beauty", "Skincare, makeup and fragrances");
        category("Books", "Stories, guides and cookbooks");
        category("Sports", "Fitness gear and outdoor equipment");
        category("Accessories", "Bags, watches and everyday carry");
        }
        categoryRepository.findAll().forEach(c -> categories.put(c.name, c));
        if (productRepository.count() == 0) {
        product("aurora-headphones", "Aurora Wireless Headphones", "Electronics", "Sonique", "Over-ear Bluetooth headphones with active noise cancelling and 30 hours of battery life.", "89.99", "129.99", 40, 4.6, 120, 0);
        product("pulse-smartwatch", "Pulse Smartwatch Series 5", "Electronics", "Zenith", "Track workouts, sleep and heart rate with a bright always-on display.", "149.00", "199.00", 25, 4.4, 95, 1);
        product("boom-speaker", "Boom Portable Speaker", "Electronics", "Sonique", "Waterproof Bluetooth speaker with deep bass and 12 hours of playtime.", "49.99", null, 60, 4.5, 150, 2);
        product("airbook-laptop", "AirBook 14 Laptop", "Electronics", "Nimbus", "Lightweight 14 inch laptop with a fast SSD, 16GB memory and all-day battery.", "749.00", "899.00", 12, 4.7, 40, 3);
        product("nova-phone", "Nova X Smartphone", "Electronics", "Nimbus", "6.5 inch OLED display, triple camera and fast charging.", "499.00", null, 30, 4.3, 80, 4);
        product("denim-jacket", "Classic Denim Jacket", "Fashion", "UrbanLine", "Timeless mid-wash denim jacket with a comfortable regular fit.", "59.99", "79.99", 35, 4.3, 60, 5);
        product("stride-sneakers", "Stride Running Sneakers", "Fashion", "Velox", "Breathable mesh sneakers with a cushioned sole for daily runs.", "74.99", null, 50, 4.5, 110, 6);
        product("cotton-tee", "Essential Cotton T-Shirt", "Fashion", "UrbanLine", "Soft 100% cotton crew neck t-shirt in a relaxed fit.", "19.99", null, 120, 4.2, 200, 7);
        product("floral-dress", "Summer Floral Dress", "Fashion", "Bloom", "Light and flowy knee-length dress with a pretty floral print.", "44.99", "59.99", 28, 4.4, 55, 8);
        product("brew-coffee-maker", "BrewMaster Coffee Maker", "Home & Kitchen", "HomeChef", "Programmable 12-cup coffee maker with a reusable filter and keep-warm plate.", "64.99", null, 22, 4.5, 70, 9);
        product("crispair-fryer", "CrispAir Fryer 4L", "Home & Kitchen", "HomeChef", "Cook crispy meals with little or no oil. Digital controls and 8 presets.", "89.99", "119.99", 18, 4.6, 130, 10);
        product("cookware-set", "Nonstick Cookware Set", "Home & Kitchen", "PanPro", "10-piece nonstick cookware set with heat-resistant handles.", "119.00", null, 15, 4.3, 35, 11);
        product("cozy-lamp", "Cozy Table Lamp", "Home & Kitchen", "Lumi", "Warm dimmable LED table lamp with a fabric shade.", "29.99", null, 45, 4.1, 48, 12);
        product("vitc-serum", "Glow Vitamin C Serum", "Beauty", "Dermaly", "Brightening face serum with vitamin C and hyaluronic acid.", "24.99", "34.99", 70, 4.5, 140, 13);
        product("velvet-lipstick", "Velvet Matte Lipstick", "Beauty", "Rouge", "Long-wearing matte lipstick in a rich, comfortable formula.", "14.99", null, 90, 4.2, 100, 14);
        product("aqua-perfume", "Aqua Bloom Eau de Parfum", "Beauty", "Essence", "A fresh floral fragrance with notes of jasmine and sea breeze.", "54.99", null, 30, 4.6, 65, 15);
        product("ion-hairdryer", "IonShine Hair Dryer", "Beauty", "Dermaly", "Ionic hair dryer with three heat settings and a cool shot button.", "39.99", "49.99", 26, 4.3, 52, 16);
        product("everyday-cookbook", "The Everyday Cookbook", "Books", "Pageturner Press", "150 simple recipes for busy weeknights, with step-by-step photos.", "22.99", null, 40, 4.4, 45, 17);
        product("midnight-stories", "Midnight Library Stories", "Books", "Pageturner Press", "A bestselling collection of short stories about second chances.", "14.99", null, 80, 4.7, 170, 18);
        product("mindful-habits", "Mindful Habits Guide", "Books", "Pageturner Press", "A practical guide to building small habits that last.", "16.99", null, 55, 4.3, 75, 19);
        product("stars-beyond", "Stars Beyond Tomorrow", "Books", "Pageturner Press", "A gripping science-fiction adventure across the galaxy.", "12.99", "15.99", 65, 4.5, 90, 20);
        product("grip-yoga-mat", "ProGrip Yoga Mat", "Sports", "FitCore", "Non-slip 6mm yoga mat with a carrying strap.", "27.99", "36.99", 55, 4.5, 105, 21);
        product("adjustable-dumbbells", "Adjustable Dumbbell Set", "Sports", "FitCore", "Space-saving adjustable dumbbells from 2 to 20 kg.", "99.00", null, 14, 4.6, 38, 22);
        product("match-football", "Match Pro Football", "Sports", "Velox", "Size 5 training football with a durable stitched cover.", "24.99", null, 40, 4.4, 62, 23);
        product("insulated-bottle", "Insulated Sports Bottle", "Sports", "HydroGo", "Keeps drinks cold for 24 hours or hot for 12. Leak-proof lid.", "17.99", null, 100, 4.5, 160, 24);
        product("urban-backpack", "Urban Travel Backpack", "Accessories", "Voyager", "Water-resistant 25L backpack with a padded laptop sleeve.", "49.99", "69.99", 38, 4.5, 98, 25);
        product("aviator-sunglasses", "Aviator Sunglasses", "Accessories", "Solis", "Polarized UV400 aviator sunglasses with a metal frame.", "34.99", null, 60, 4.3, 57, 26);
        product("leather-wallet", "Leather Bifold Wallet", "Accessories", "Voyager", "Slim genuine leather wallet with RFID protection.", "29.99", null, 70, 4.4, 85, 27);
        product("analog-watch", "Minimalist Analog Watch", "Accessories", "Chronos", "Elegant analog watch with a stainless steel case and leather strap.", "79.99", "99.99", 24, 4.5, 72, 28);
            System.out.println("Demo catalog created: " + productRepository.count() + " products.");
        }
        System.out.println("Demo admin login -> admin@shop.com / Admin@123");
    }

    private void createUser(String name, String email, String password, String phone, String address, Role role) {
        if (userRepository.existsByEmail(email)) return;
        User u = new User();
        u.name = name;
        u.email = email;
        u.password = passwordEncoder.encode(password);
        u.phone = phone;
        u.address = address;
        u.role = role;
        userRepository.save(u);
    }

    private void category(String name, String description) {
        Category c = new Category();
        c.name = name;
        c.description = description;
        categoryRepository.save(c);
    }

    private void product(String slug, String name, String category, String brand, String description,
                         String price, String oldPrice, int stock, double rating, int sales, int index) {
        Product p = new Product();
        p.name = name;
        p.description = description;
        p.price = new BigDecimal(price);
        p.oldPrice = oldPrice == null ? null : new BigDecimal(oldPrice);
        p.category = categories.get(category);
        p.brand = brand;
        p.imageUrl = "images/products/" + slug + ".svg";
        p.stockQuantity = stock;
        p.rating = rating;
        p.salesCount = sales;
        p.createdAt = LocalDateTime.now().minusHours(index * 8L);
        productRepository.save(p);
    }
}
