package ch.puzzle.debezium.event;

import ch.puzzle.debezium.article.entity.Article;
import ch.puzzle.debezium.order.entity.ShopOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;

import java.time.Instant;
import java.util.UUID;

public class OrderCreatedEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private static final String TYPE = "Order";
    private static final String EVENT_TYPE = "OrderCreated";

    private final UUID id;
    private final Long orderId;
    private final JsonNode jsonNode;
    private final Instant timestamp;

    public OrderCreatedEvent(Instant created, ShopOrder shopOrder) {
        this.id = UUID.randomUUID();
        this.orderId = shopOrder.id;
        this.timestamp = created;
        this.jsonNode = asJson(shopOrder);
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(orderId);
    }

    @Override
    public String getAggregateType() {
        return TYPE;
    }

    @Override
    public String getType() {
        return EVENT_TYPE;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return jsonNode;
    }

    public ObjectNode asJson(ShopOrder order) {
        ObjectNode asJson = mapper.createObjectNode()
                .put("id", order.id)
                .put("status", order.getStatus().name());

        ArrayNode items = asJson.putArray("articleOrders");

        for (Article article : order.getArticles()) {
            items.add(
                    mapper.createObjectNode()
                            .put("id", article.id)
                            .put("name", article.getName())
                            .put("price", article.getPrice())
            );
        }

        return asJson;
    }

}
