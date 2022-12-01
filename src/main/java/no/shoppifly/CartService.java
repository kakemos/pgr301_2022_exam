package no.shoppifly;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface CartService {

    Cart getCart(String id);

    Cart update(Cart cart);

    String checkout(Cart cart);

    List<String> getAllsCarts();

    Map<String, Cart> getAllCarts();
}
