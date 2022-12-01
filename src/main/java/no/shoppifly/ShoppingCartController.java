package no.shoppifly;

import io.micrometer.core.annotation.Timed;
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
import java.util.Map;

@RestController()
public class ShoppingCartController implements ApplicationListener<ApplicationReadyEvent> {

    private final Counter checkoutCounter;
    private final Timer checkoutTimer;

    @Autowired
    private MeterRegistry meterRegistry;
    private final CartService cartService;

    @Autowired
    public ShoppingCartController(CartService cartService, MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.cartService = cartService;

        checkoutCounter = meterRegistry.counter("checkout_count");
        checkoutTimer = meterRegistry.timer("checkout_latency");
    }

    @GetMapping(path = "/cart/{id}", consumes = "application/json", produces = "application/json")
    public Cart getCart(@PathVariable String id) {
        return cartService.getCart(id);
    }

    /**
     * Checks out a shopping cart. Removes the cart, and returns an order ID
     *
     * @return an order ID
     */
    @Timed
    @PostMapping(path = "/cart/checkout", consumes = "application/json", produces = "application/json")
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
    @PostMapping(path = "/cart", consumes = "application/json", produces = "application/json")
    public Cart updateCart(@RequestBody Cart cart) {
        return cartService.update(cart);
    }

    /**
     * return all cart IDs
     *
     * @return
     */
    @GetMapping(path = "/carts", consumes = "application/json", produces = "application/json")
    public List<String> getAllCarts() {
        return cartService.getAllsCarts();
    }

    @GetMapping(path = "/all-carts", consumes = "application/json", produces = "application/json")
    public Map<String, Cart> getAlllCarts() {
        return cartService.getAllCarts();
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Gauge.builder("cart_count", cartService,
                c -> c.getAllsCarts().size()).register(meterRegistry);

        Gauge.builder("carts_value", cartService,
                s -> s.getAllCarts()
                        .values()
                        .stream()
                        .flatMap(c -> c.getItems().stream()
                                .map(i -> i.getUnitPrice() * i.getQty()))
                        .reduce(0f, Float::sum))
                .register(meterRegistry);
    }
}