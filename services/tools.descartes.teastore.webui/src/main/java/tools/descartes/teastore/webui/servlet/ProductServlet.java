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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tools.descartes.teastore.webui.servlet.elhelper.ELHelperUtils;
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
      request.setAttribute("CategoryList", getCategories());
      request.setAttribute("title", "TeaStore Product");
      request.setAttribute("login", isUserLoggedInLocal(request));
      Product p = HTTPClient.getProduct(id);
      request.setAttribute("product", p);
      request.setAttribute("productImage", getProductFullImage(p));
      request.setAttribute("storeIcon", ICON_URL);
      request.setAttribute("helper", ELHelperUtils.UTILS);
      request.setAttribute("productPreviewImageBaseURL", getProductPreviewImageBaseURL());

      request.getRequestDispatcher("pages/product.jsp").forward(request, response);
    } else {
      redirect("/", response);
    }
  }

  private String getProductPreviewImageBaseURL() {
    String imageBaseURL = Service.getServiceBaseURL("IMAGE_CDN_HOST", "IMAGE_CDN_PORT");
    String imageContext = Service.IMAGE.getServiceName();
    return String.format("%s/%s/preview/", imageBaseURL, imageContext);
  }

  private String getProductFullImage(Product product) {
    String imageBaseURL = Service.getServiceBaseURL("IMAGE_CDN_HOST", "IMAGE_CDN_PORT");
    String imageContext = Service.IMAGE.getServiceName();
    return String.format("%s/%s/full/%s.png", imageBaseURL, imageContext, product.getId());
  }
}
