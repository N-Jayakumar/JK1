package com.jk1.service.impl;

import com.jk1.entity.*;
import com.jk1.entity.enums.OrderStatus;
import com.jk1.entity.enums.PaymentMethod;
import com.jk1.entity.enums.PaymentStatus;
import com.jk1.repository.*;
import com.jk1.service.CartService;
import com.jk1.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> findAll(Specification<Order> spec, Pageable pageable) {
        return orderRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    @Override
    public Order placeOrder(String email, com.jk1.dto.request.OrderRequestDTO dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartService.getCartForUser(email);
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Address address = addressRepository.findById(dto.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Invalid address");
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        order.setUser(user);
        order.setShippingAddress(address);
        order.setOrderStatus(OrderStatus.PENDING);
        
        BigDecimal total = cart.getTotalAmount();
        
        if (dto.getCouponId() != null) {
            Coupon coupon = couponRepository.findById(dto.getCouponId()).orElse(null);
            if (coupon != null) {
                order.setCoupon(coupon);
                // Apply dummy logic for now
                total = total.subtract(coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() : BigDecimal.ZERO);
                if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
            }
        }
        
        order.setTotalAmount(total);

        // Add Items and verify inventory
        for (CartItem ci : cart.getItems()) {
            Product product = ci.getProduct();
            if (product.getQuantity() == null || product.getQuantity() < ci.getQuantity()) {
                throw new RuntimeException("Product " + product.getName() + " is out of stock");
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setPrice(ci.getPrice());
            orderItem.setSelectedSize(ci.getSelectedSize());
            orderItem.setSelectedColor(ci.getSelectedColor());
            order.getItems().add(orderItem);
            
            // Deduct stock
            product.setQuantity(product.getQuantity() - ci.getQuantity());
            productRepository.save(product);
        }

        Order savedOrder = orderRepository.save(order);

        // Create Payment
        PaymentMethod paymentMethod = PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase());
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(total);
        payment.setPaymentMethod(paymentMethod);
        
        if (paymentMethod == PaymentMethod.CREDIT_CARD || paymentMethod == PaymentMethod.DEBIT_CARD || paymentMethod == PaymentMethod.UPI) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            payment.setPaymentStatus(PaymentStatus.PENDING); // COD
            payment.setTransactionId("COD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        paymentRepository.save(payment);
        
        // Clear Cart
        cartService.clearCart(email);

        return savedOrder;
    }

    @Override
    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserId(user.getId());
    }

    @Override
    public Order getOrderDetails(String email, String orderNumber) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
                
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        return order;
    }
}
