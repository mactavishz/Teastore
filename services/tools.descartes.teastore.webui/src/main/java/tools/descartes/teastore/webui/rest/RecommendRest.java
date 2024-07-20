package tools.descartes.teastore.webui.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.webui.restclient.HTTPClient;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@Path("recommendation")
public class RecommendRest {

      @GET
    @Path("ads")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getRecommendation(@CookieParam("sessionBlob") String cookieValue, @QueryParam("pid") long pid) {
          List<Product> result = new LinkedList<>();
        if (cookieValue == null || cookieValue.isEmpty()) {
            return result;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String decodedCookie = URLDecoder.decode(cookieValue, StandardCharsets.UTF_8);
            SessionBlob blob = objectMapper.readValue(decodedCookie, SessionBlob.class);
            if (blob != null) {
                List<OrderItem> items = new LinkedList<>();
                if (pid != 0) {
                    OrderItem oi = new OrderItem();
                    oi.setProductId(pid);
                    oi.setQuantity(1);
                    items.add(oi);
                }
                items.addAll(blob.getOrderItems());
                List<Long> productIds = HTTPClient.getRecommendations(items, blob.getUid());
                for (Long productId : productIds) {
                    result.add(HTTPClient.getProduct(productId));
                }
                if (result.size() > 3) {
                    result.subList(3, result.size()).clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return result;
    }
}
