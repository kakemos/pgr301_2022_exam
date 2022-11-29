package no.shoppifly;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@RestController()
public class ShoppingCartController implements ApplicationListener<ApplicationReadyEvent> {

    private Counter checkoutCounter;
    private Timer checkoutTimer;

    @Autowired
    private MeterRegistry meterRegistry;
    private final CartService cartService;

    public ShoppingCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Autowired
    public ShoppingCartController(CartService cartService, MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.cartService = cartService;

        checkoutCounter = meterRegistry.counter("checkout_count");
        checkoutTimer = meterRegistry.timer("checkout_latency");
    }

    @GetMapping(path = "/cart/{id}")
    public Cart getCart(@PathVariable String id) {
        return cartService.getCart(id);
    }

    /**
     * Checks out a shopping cart. Removes the cart, and returns an order ID
     *
     * @return an order ID
     */
    @PostMapping(path = "/cart/checkout")
    public String checkout(@RequestBody Cart cart) {
        long startTime = System.currentTimeMillis();
        checkoutCounter.increment();
        checkoutTimer.record(Duration.ofMillis(System.currentTimeMillis() - startTime));

        return cartService.checkout(cart);
    }

    /**
     * Updates a shopping cart, replacing it's contents if it already exists. If no cart exists (id is null)
     * a new cart is created.
     *
     * @return the updated cart
     */
    @PostMapping(path = "/cart")
    public Cart updateCart(@RequestBody Cart cart) {
        return cartService.update(cart);
    }

    /**
     * return all cart IDs
     *
     * @return
     */
    @GetMapping(path = "/carts")
    public List<String> getAllCarts() {
        return cartService.getAllsCarts();
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Gauge.builder("cart_count", cartService,
                s -> s.getAllsCarts().size()).register(meterRegistry);

        /*
        Gauge.builder("carts_value", cartService,
                s -> s.getAllsCarts()
                        .stream()
                        .map(Cart::getItems)
                        .map(Item::getUnitPrice)
                        .mapToDouble(BigDecimal::doubleValue)
                        .sum())
                .register(meterRegistry);
         */
    }
}