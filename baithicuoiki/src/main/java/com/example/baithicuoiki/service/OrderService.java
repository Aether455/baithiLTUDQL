package com.example.baithicuoiki.service;

import com.example.baithicuoiki.model.*;
import com.example.baithicuoiki.repository.CustomerRepository;
import com.example.baithicuoiki.repository.OrderDetailRepository;
import com.example.baithicuoiki.repository.OrderRepository;
import com.example.baithicuoiki.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Order createOrder(String customerName,String shippingAddress, String phoneNumber, String notes, String paymentMethod, List<CartItem> cartItems, User user) {
        Order order = new Order();
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setCustomerName(customerName);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setUser(user);

        // Xác định Customer từ User nếu có
        if (user != null) {
            Optional<Customer> customerOpt = customerRepository.findByUserId(user.getId());
            Customer customer;
            if (customerOpt.isPresent()) {
                customer = customerOpt.get();
            }
            else {
                // Nếu chưa có Customer, tạo mới với giá trị mặc định
                customer = new Customer();
                customer.setUser(user);
                customer.setFullName(user.getUsername());  // Mặc định là username
                customer.setAddress("unknown");  // Mặc định là "unknown"
                customer.setTotalSpent(0.0);
            }

            // Chỉ cập nhật nếu giá trị vẫn còn là mặc định
            if ("unknown".equalsIgnoreCase(customer.getAddress())) {
                customer.setAddress(shippingAddress);
            }
            if (customer.getFullName().equals(user.getUsername())) {
                customer.setFullName(customerName);
            }

            customerRepository.save(customer);
            order.setCustomer(customer);
        }

        order.setTotalPrice(0.0);
        order = orderRepository.save(order);

        double orderTotalPrice = 0.0;


        for (CartItem item : cartItems) {
            if (item.getProduct().getQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Sản phẩm " + item.getProduct().getId() + " không đủ số lượng.");
            }
        }

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            Product product = item.getProduct();

            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setTotalPrice(detail.calculateTotalPrice());

            orderTotalPrice += detail.getTotalPrice();

            // Giảm số lượng sản phẩm trong kho
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            orderDetailRepository.save(detail);
        }
        order.setTotalPrice(orderTotalPrice);
        orderRepository.save(order); // **Cập nhật lại tổng giá sau khi thêm OrderDetail**

        // Cập nhật tổng chi tiêu của khách hàng
        if (order.getCustomer() != null) {
            order.getCustomer().updateTotalSpent();
            customerRepository.save(order.getCustomer());
        }

        // Xóa giỏ hàng sau khi đặt hàng thành công
        if (user != null) {
            cartService.clearCard(user);
        }

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order with ID " + orderId + " does not exist."));
    }

    public List<OrderDetail> getOrderDetails(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order != null) {
            orderRepository.deleteById(orderId);

            // Cập nhật tổng chi tiêu của khách hàng sau khi xóa đơn
            if (order.getCustomer() != null) {
                order.getCustomer().updateTotalSpent();
                customerRepository.save(order.getCustomer());
            }
        }
    }
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
