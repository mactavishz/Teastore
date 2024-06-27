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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import tools.descartes.teastore.auth.restclient.PersistenceClient;
import tools.descartes.teastore.auth.security.BCryptProvider;
import tools.descartes.teastore.auth.security.RandomSessionIdGenerator;
import tools.descartes.teastore.auth.security.ShaSecurityProvider;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.User;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.utils.NotFoundException;
import tools.descartes.teastore.utils.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.metrics.annotation.Counted;

/**
 * Rest endpoint for the store user actions.
 * 
 * @author Simon
 */
@Path("useractions")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class AuthUserActionsRest {
  private final PersistenceClient persistenceClient = new PersistenceClient();
  private final Logger LOG = LoggerFactory.getLogger(AuthUserActionsRest.class);

  /**
   * Persists order in database.
   * 
   * @param blob
   *          SessionBlob
   * @param totalPriceInCents
   *          totalPrice
   * @param addressName
   *          address
   * @param address1
   *          address
   * @param address2
   *          address
   * @param creditCardCompany
   *          creditcard
   * @param creditCardNumber
   *          creditcard
   * @param creditCardExpiryDate
   *          creditcard
   * @return Response containing SessionBlob
   */
  @POST
  @Path("placeorder")
  @Timed(

          name = "placeOrderTimer",
          tags = {"method=post"},
          absolute = true,
          description = "Time and Frequency of placeOrder"

  )
  @Counted(
          name = "placeOrderCounter",
          tags = {"method=post"},
          absolute = true,
          description = "Counts the number of invocations of placeOrder"
  )
  public Response placeOrder(SessionBlob blob,
      @QueryParam("totalPriceInCents") long totalPriceInCents,
      @QueryParam("addressName") String addressName, @QueryParam("address1") String address1,
      @QueryParam("address2") String address2,
      @QueryParam("creditCardCompany") String creditCardCompany,
      @QueryParam("creditCardNumber") String creditCardNumber,
      @QueryParam("creditCardExpiryDate") String creditCardExpiryDate) {
    if (new ShaSecurityProvider().validate(blob) == null || blob.getOrderItems().isEmpty()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    blob.getOrder().setUserId(blob.getUid());
    blob.getOrder().setTotalPriceInCents(totalPriceInCents);
    blob.getOrder().setAddressName(addressName);
    blob.getOrder().setAddress1(address1);
    blob.getOrder().setAddress2(address2);
    blob.getOrder().setCreditCardCompany(creditCardCompany);
    blob.getOrder().setCreditCardExpiryDate(creditCardExpiryDate);
    blob.getOrder().setCreditCardNumber(creditCardNumber);
    blob.getOrder().setTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    long orderId;
    try {
      orderId = persistenceClient.createOrder(blob.getOrder());
    } catch (NotFoundException e) {
      return Response.status(404).build();
    }
    for (OrderItem item : blob.getOrderItems()) {
      try {
        item.setOrderId(orderId);
        persistenceClient.createOrderItem(item);;
      } catch (TimeoutException e) {
        return Response.status(408).build();
      } catch (NotFoundException e) {
        return Response.status(404).build();
      }
    }
    blob.setOrder(new Order());
    blob.getOrderItems().clear();
    blob = new ShaSecurityProvider().secure(blob);
    return Response.status(Response.Status.OK).entity(blob).build();
  }

  /**
   * User login.
   * 
   * @param blob
   *          SessionBlob
   * @param name
   *          Username
   * @param password
   *          password
   * @return Response with SessionBlob containing login information.
   */
  @POST
  @Path("login")
  @Timed(

          name = "loginTimer",
          tags = {"method=post"},
          absolute = true,
          description = "Time and Frequency of login"

  )
  @Counted(
          name = "loginCounter",
          tags = {"method=post"},
          absolute = true,
          description = "Counts the number of invocations of login"
  )
  public Response login(SessionBlob blob, @QueryParam("name") String name,
      @QueryParam("password") String password) {
    User user;
    try {
      user = persistenceClient.getUser("name", name);
    } catch (TimeoutException e){
      return Response.status(408).build();
    } catch (NotFoundException e) {
      return Response.status(Response.Status.OK).entity(blob).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    if (user != null && BCryptProvider.checkPassword(password, user.getPassword())
    ) {
      blob.setUid(user.getId());
      blob.setSid(new RandomSessionIdGenerator().getSessionId());
      blob = new ShaSecurityProvider().secure(blob);
      return Response.status(Response.Status.OK).entity(blob).build();
    }
    return Response.status(Response.Status.OK).entity(blob).build();
  }

  /**
   * User logout.
   * 
   * @param blob
   *          SessionBlob
   * @return Response with SessionBlob
   */
  @POST
  @Path("logout")
  @Timed(

          name = "logoutTimer",
          tags = {"method=post"},
          absolute = true,
          description = "Time and Frequency of logout"

  )
  @Counted(
          name = "logoutCounter",
          tags = {"method=post"},
          absolute = true,
          description = "Counts the number of invocations of logout"
  )
  public Response logout(SessionBlob blob) {
    blob.setUid(null);
    blob.setSid(null);
    blob.setOrder(new Order());
    blob.getOrderItems().clear();
    return Response.status(Response.Status.OK).entity(blob).build();
  }

  /**
   * Checks if user is logged in.
   * 
   * @param blob
   *          Sessionblob
   * @return Response with true if logged in
   */
  @POST
  @Path("isloggedin")
  public Response isLoggedIn(SessionBlob blob) {
    return Response.status(Response.Status.OK).entity(new ShaSecurityProvider().validate(blob))
        .build();
  }

}
