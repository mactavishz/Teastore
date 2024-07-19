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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tools.descartes.teastore.webui.servlet.elhelper.ELHelperUtils;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.webui.restclient.HTTPClient;

/**
 * Servlet implementation for the web view of "Product".
 * 
 * @author Andre Bauer
 */
@WebServlet("/product")
public class ProductServlet extends AbstractUIServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public ProductServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    checkforCookie(request, response);
    if (request.getParameter("id") != null) {
      Long id = Long.valueOf(request.getParameter("id"));
      request.setAttribute("CategoryList", HTTPClient.getCategories(-1, -1));
      request.setAttribute("title", "TeaStore Product");
      request.setAttribute("login", HTTPClient.isLoggedIn(getSessionBlob(request)));
      Product p = HTTPClient.getProduct(id);
      request.setAttribute("product", p);

      List<OrderItem> items = new LinkedList<>();
      OrderItem oi = new OrderItem();
      oi.setProductId(id);
      oi.setQuantity(1);
      items.add(oi);
      items.addAll(getSessionBlob(request).getOrderItems());
      List<Long> productIds = HTTPClient.getRecommendations(items, getSessionBlob(request).getUid());
      List<Product> ads = new LinkedList<Product>();
      for (Long productId : productIds) {
        ads.add(HTTPClient.getProduct(productId));
      }

      if (ads.size() > 3) {
        ads.subList(3, ads.size()).clear();
      }
      request.setAttribute("Advertisment", ads);

      request.setAttribute("productImages", getProductPreviewImagesMap(ads));
      request.setAttribute("productImage", getProductFullImage(p));
      request.setAttribute("storeIcon", String.format("/%s/images/icon.png", Service.WEBUI.getServiceName()));
      request.setAttribute("helper", ELHelperUtils.UTILS);

      request.getRequestDispatcher("WEB-INF/pages/product.jsp").forward(request, response);
    } else {
      redirect("/", response);
    }
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

  private String getProductFullImage(Product product) {
    String imageBaseURL = Service.getServiceBaseURL("IMAGE_CDN_HOST", "IMAGE_CDN_PORT");
    String imageContext = Service.IMAGE.getServiceName();
    return String.format("%s/%s/full/%s.png", imageBaseURL, imageContext, product.getId());
  }
}
