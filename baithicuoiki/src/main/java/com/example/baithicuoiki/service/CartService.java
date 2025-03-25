package com.example.baithicuoiki.service;

import com.example.baithicuoiki.model.Cart;
import com.example.baithicuoiki.model.CartItem;
import com.example.baithicuoiki.model.Product;
import com.example.baithicuoiki.model.User;
import com.example.baithicuoiki.repository.CartItemRepository;
import com.example.baithicuoiki.repository.CartRepository;
import com.example.baithicuoiki.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();
    @Autowired // Tiêm phụ thuộc tự động cho CartRepository.
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;


    public Cart getOrCreateCart(User user){
        return cartRepository.findByUser(user)
                .orElseGet(()->{ // nếu Không tồn tại cart vơi user tương ứng thì sẽ tạo mới
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }
    @Transactional // Đảm bảo các thay đổi database thực hiện đồng thời hoặc không thực hiện nếu có lỗi.
    public void addToCart(User user,Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        Cart cart = getOrCreateCart(user);// lay hoac tao cart
        List<CartItem> cartItems = cart.getItems(); // Lấy danh sách các sản phẩm trong giỏ hàng từ Cart
        // Check if the product is already in the cart
        CartItem existingItem = cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Nếu có, cập nhật số lượng và tổng giá.
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.updateTotalPrice();
        } else {
            // Nếu chưa có, tạo item mới và thêm vào giỏ hàng.
            CartItem newItem = new CartItem(quantity, product, cart);
            cart.getItems().add(newItem);
        }
        cartRepository.save(cart); // Lưu giỏ hàng vào database.

    }
    // Lấy danh sách các CartItem trong giỏ hàng của người dùng.
    public List<CartItem> getCartItems(User user) {
        return getOrCreateCart(user).getItems();
    }
    @Transactional
    public void removeFromCart(User user, Long productId) {
        // Tìm giỏ hàng của người dùng.
        Cart cart = getOrCreateCart(user);
        cart.getItems().removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));
        cartRepository.save(cart);//luu gio hang da cap nhat
    }

    // Tính tổng giá của tất cả sản phẩm trong giỏ hàng của người dùng.
    public double getTotalCartPrice(User user) {
        return getCartItems(user).stream()
                .mapToDouble(CartItem::getTotalPrice) // Lấy tổng giá của mỗi CartItem.
                .sum(); // Cộng dồn để có tổng giá của cả giỏ hàng.
    }

    public void clearCard(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public void updateCartItemQuantity(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0.");
        }

        Cart cart = getOrCreateCart(user);
        List<CartItem> cartItems = cart.getItems();

        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại trong giỏ hàng."));

        cartItem.setQuantity(quantity);
        cartItem.updateTotalPrice();

        cartRepository.save(cart);
    }


    public Long countCartItem(String username){
        return cartItemRepository.countCartItemByUsername(username);
    }



    public boolean isStockAvailable(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));
        return product.getQuantity() >= quantity;
    }

}
