package wsdemo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.inject.Singleton;


@Singleton
public class BidService {
  private static final Logger log = Logger.getLogger(BidService.class.getName());

  private final Map<Integer, List<String>> productToUserMap = new HashMap<>();
  private final Map<String, BiConsumer<Integer, BigDecimal>> subscriptions = new HashMap<>();

  public BidService() {
    Timer timer = new Timer();
    timer.schedule(new GeneratePricesTask(productToUserMap, subscriptions), 0, 5000);
  }

  public void placeBid(final int productId, final String userId) {
    productToUserMap.computeIfAbsent(productId, key -> new ArrayList<>()).add(userId);
    log.info("User " + userId + " placed a bid " + productId);
  }

  public void subscribe(final String userId, final BiConsumer<Integer, BigDecimal> consumer) {
    subscriptions.computeIfAbsent(userId, key -> consumer);
    log.info("User " + userId + " subscribed to bid notifications");
  }

  public void unsubscribe(final String userId) {
    subscriptions.remove(userId);
    log.info("User " + userId + " unsubscribed from bid notifications");
  }

  static class GeneratePricesTask extends TimerTask {
    private final Map<Integer, List<String>> productToUserMap;
    private final Map<String, BiConsumer<Integer, BigDecimal>> subscriptions;
    private final Random random = new Random();

    GeneratePricesTask(final Map<Integer, List<String>> productToUserMap,
                       final Map<String, BiConsumer<Integer, BigDecimal>> subscriptions) {
      this.productToUserMap = productToUserMap;
      this.subscriptions = subscriptions;
    }

    @Override
    public void run() {
      productToUserMap.keySet().stream().forEach(productId -> {
        BigDecimal newPrice = new BigDecimal(random.nextDouble() * 100);

        // In a real auction it should be called after receiving JMS message.
        notifySubscribers(productId, newPrice);
      });
    }

    private void notifySubscribers(int productId, BigDecimal price) {
      // Get all users who placed the bid.
      productToUserMap.getOrDefault(productId, Collections.<String>emptyList()).stream()
          // Find out if they subscribed to updates (WebSocket session still alive).
          .filter(subscriptions::containsKey)
          // Notify them.
          .forEach(userId -> subscriptions.get(userId).accept(productId, price));
    }
  }
}
