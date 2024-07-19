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
import java.util.LinkedList;
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

    request.setAttribute("storeIcon", String.format("/%s/images/icon.png", Service.WEBUI.getServiceName()));
    request.setAttribute("title", "TeaStore Cart");
    request.setAttribute("CategoryList", HTTPClient.getCategories(-1, -1));
    request.setAttribute("OrderItems", orderItems);
    request.setAttribute("Products", products);
    request.setAttribute("login", HTTPClient.isLoggedIn(getSessionBlob(request)));

    List<Long> productIds = HTTPClient.getRecommendations(blob.getOrderItems(), blob.getUid());
    List<Product> ads = new LinkedList<Product>();
    for (Long productId : productIds) {
      ads.add(HTTPClient.getProduct(productId));
    }

    if (ads.size() > 3) {
      ads.subList(3, ads.size()).clear();
    }
    request.setAttribute("Advertisment", ads);
    request.setAttribute("productImages", getProductPreviewImagesMap(ads));
    request.getRequestDispatcher("WEB-INF/pages/cart.jsp").forward(request, response);

  }

  private HashMap<Long, String> getProductPreviewImagesMap(List<Product> products) {
    HashMap<Long, String> productImages = new HashMap<>();
    String imageBaseURL = Service.getServiceBaseURL("IMAGE_CDN_HOST", "IMAGE_CDN_PORT");
    String imageContext = Service.IMAGE.getServiceName();
    for (Product product : products) {
      productImages.put(product.getId(), String.format("%s/%s/preview/%s.png", imageBaseURL, imageContext, product.getId()));
    }
    return productImages;
  }
}
