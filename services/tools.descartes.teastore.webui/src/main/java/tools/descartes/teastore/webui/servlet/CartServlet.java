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

package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.webui.restclient.HTTPClient;

/**
 * Servlet implementation for the web view of "Cart".
 * 
 * @author Andre Bauer
 */
@WebServlet("/cart")
public class CartServlet extends AbstractUIServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public CartServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    checkforCookie(request, response);
    SessionBlob blob = getSessionBlob(request);

    List<OrderItem> orderItems = blob.getOrderItems();
    ArrayList<Long> ids = new ArrayList<Long>();
    for (OrderItem orderItem : orderItems) {
      ids.add(orderItem.getProductId());
    }

    HashMap<Long, Product> products = new HashMap<Long, Product>();
    for (Long id : ids) {
      Product product = HTTPClient.getProduct(id);
      products.put(product.getId(), product);
    }

    request.setAttribute("storeIcon", ICON_URL);
    request.setAttribute("title", "TeaStore Cart");
    request.setAttribute("CategoryList", getCategories());
    request.setAttribute("OrderItems", orderItems);
    request.setAttribute("Products", products);
    request.setAttribute("login", HTTPClient.isLoggedIn(getSessionBlob(request)));
    request.setAttribute("productPreviewImageBaseURL", getProductPreviewImageBaseURL());
    request.getRequestDispatcher("pages/cart.jsp").forward(request, response);
  }


  private String getProductPreviewImageBaseURL() {
    String imageBaseURL = Service.getServiceBaseURL("IMAGE_CDN_HOST", "IMAGE_CDN_PORT");
    String imageContext = Service.IMAGE.getServiceName();
    return String.format("%s/%s/preview/", imageBaseURL, imageContext);
  }
}
