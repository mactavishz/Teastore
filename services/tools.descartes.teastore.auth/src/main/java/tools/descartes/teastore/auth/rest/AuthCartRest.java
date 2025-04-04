/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.descartes.teastore.auth.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import tools.descartes.teastore.auth.restclient.HTTPClient;
import tools.descartes.teastore.auth.security.ShaSecurityProvider;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.utils.NotFoundException;
import tools.descartes.teastore.utils.TimeoutException;
import org.eclipse.microprofile.metrics.annotation.Timed;


/**
 * Rest endpoint for the store cart.
 * 
 * @author Simon
 */
@Path("cart")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class AuthCartRest {

  /**
   * Adds product to cart. If the product is already in the cart the quantity is
   * increased.
   * 
   * @param blob
   *          Sessionblob
   * @param pid
   *          productid
   * @return Response containing session blob with updated cart
   */
  @POST
  @Path("add/{pid}")
  @Timed(
          name = "addProductToCart",
          tags = {"method=post", "url=/cart/add/{pid}"},
          absolute = true,
          description = "Time and Frequency of addProductToCart"
  )
  public Response addProductToCart(SessionBlob blob, @PathParam("pid") final Long pid) {

    Product product;
    try {
      product = HTTPClient.getProduct(pid);
    } catch (TimeoutException e) {
      return Response.status(408).build();
    } catch (NotFoundException e) {
      return Response.status(404).build();
    }

    for (OrderItem orderItem : blob.getOrderItems()) {
      if (orderItem.getProductId() == pid) {
        orderItem.setQuantity(orderItem.getQuantity() + 1);
        blob = new ShaSecurityProvider().secure(blob);
        return Response.status(Response.Status.OK).entity(blob).build();
      }
    }
    OrderItem item = new OrderItem();
    item.setProductId(pid);
    item.setQuantity(1);
    item.setUnitPriceInCents(product.getListPriceInCents());
    blob.getOrderItems().add(item);
    blob = new ShaSecurityProvider().secure(blob);
    return Response.status(Response.Status.OK).entity(blob).build();
  }

  /**
   * Remove product from cart.
   * 
   * @param blob
   *          Sessionblob
   * @param pid
   *          product id
   * @return Response containing Sessionblob with updated cart
   */
  @POST
  @Path("remove/{pid}")
  @Timed(
          name = "removeProductFromCart",
          tags = {"method=post", "url=/cart/remove/{pid}"},
          absolute = true,
          description = "Time and Frequency of removeProductFromCart"
  )
  public Response removeProductFromCart(SessionBlob blob, @PathParam("pid") final Long pid) {
    OrderItem toRemove = null;
    for (OrderItem item : blob.getOrderItems()) {
      if (item.getProductId() == pid) {
        toRemove = item;
      }
    }
    if (toRemove != null) {
      blob.getOrderItems().remove(toRemove);
      blob = new ShaSecurityProvider().secure(blob);
      return Response.status(Response.Status.OK).entity(blob).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  /**
   * Updates quantity of product in cart.
   * 
   * @param blob
   *          Sessionblob
   * @param pid
   *          Productid
   * @param quantity
   *          New quantity
   * @return Response containing Sessionblob with updated cart
   */
  @PUT
  @Path("{pid}")
  @Timed(
          name = "updateQuantity",
          tags = {"method=put", "url=/cart/{pid}"},
          absolute = true,
          description = "Time and Frequency of updateQuantity"
  )
  public Response updateQuantity(SessionBlob blob, @PathParam("pid") final Long pid,
      @QueryParam("quantity") int quantity) {
    for (OrderItem item : blob.getOrderItems()) {
      if (item.getProductId() == pid) {
        item.setQuantity(quantity);
        blob = new ShaSecurityProvider().secure(blob);
        return Response.status(Response.Status.OK).entity(blob).build();
      }
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }

}
