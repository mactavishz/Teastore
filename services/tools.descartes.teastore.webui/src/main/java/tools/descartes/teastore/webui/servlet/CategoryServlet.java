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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.webui.restclient.HTTPClient;

/**
 * Servlet implementation for the web view of "Category".
 * 
 * @author Andre Bauer
 */
@WebServlet("/category")
public class CategoryServlet extends AbstractUIServlet {
  private static final long serialVersionUID = 1L;
  private static final int INITIAL_PRODUCT_DISPLAY_COUNT = 20;
  private static final List<Integer> PRODUCT_DISPLAY_COUNT_OPTIONS = Arrays.asList(5, 10, 20, 30,
      50);

  /**
   * @see HttpServlet#HttpServlet()
   */
  public CategoryServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException{
    if (request.getParameter("category") != null) {
      checkforCookie(request, response);

      long categoryID = Long.parseLong(request.getParameter("category"));
      Category category = HTTPClient.getCategory(categoryID);
      int products = HTTPClient.getProductsCount(categoryID);
      int numberProducts = INITIAL_PRODUCT_DISPLAY_COUNT;
      if (request.getAttribute("numberProducts") != null) {
        numberProducts = Integer.parseInt(request.getAttribute("numberProducts").toString());
      }

      int page = 1;
      if (request.getParameter("page") != null) {
        int pagenumber = Integer.parseInt(request.getParameter("page"));
        int maxpages = (int) Math.ceil(((double) products) / numberProducts);
        if (pagenumber <= maxpages) {
          page = pagenumber;
        }
      }

      ArrayList<String> navigation = createNavigation(products, page, numberProducts);

      List<Product> productlist = HTTPClient.getProductsByCategory(categoryID, (page - 1) * numberProducts, numberProducts);
      request.setAttribute("productImages", getProductPreviewImagesMap(productlist));
      request.setAttribute("CategoryList", getCategories());
      request.setAttribute("title", "TeaStore Categorie " + category.getName());
      request.setAttribute("login", isUserLoggedInLocal(request));
      request.setAttribute("storeIcon", ICON_URL);
      request.setAttribute("Productslist", productlist);
      request.setAttribute("category", category.getName());
      request.setAttribute("categoryID", categoryID);
      request.setAttribute("currentnumber", numberProducts);
      request.setAttribute("pagination", navigation);
      request.setAttribute("pagenumber", page);
      request.setAttribute("productdisplaycountoptions", PRODUCT_DISPLAY_COUNT_OPTIONS);
      request.getRequestDispatcher("pages/category.jsp").forward(request, response);
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handlePOSTRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getParameter("number") != null && request.getParameter("page") != null
        && request.getParameter("category") != null) {
      redirect(
          "/category?category=" + request.getParameter("category") + "&page="
              + request.getParameter("page"),
          response, PRODUCTCOOKIE, request.getParameter("number"));
    } else {
      handleGETRequest(request, response);
    }
  }

  /**
   * Creates the entries for the pagination.
   * @param products
   * @param page
   * @param numberProducts
   * @return Arraylist<String> pagination
   */
  private ArrayList<String> createNavigation(int products, int page, int numberProducts) {

    ArrayList<String> navigation = new ArrayList<String>();

    int numberpagination = 5;

    int maxpages = (int) Math.ceil(((double) products) / numberProducts);

    if (maxpages < page) {
      return navigation;
    }

    if (page == 1) {
      if (maxpages == 1) {
        navigation.add("1");
        return navigation;
      }
      int min = Math.min(maxpages, numberpagination + 1);
      for (int i = 1; i <= min; i++) {
        navigation.add(String.valueOf(i));
      }

    } else {
      navigation.add("previous");
      if (page == maxpages) {
        int max = Math.max(maxpages - numberpagination, 1);
        for (int i = max; i <= maxpages; i++) {
          navigation.add(String.valueOf(i));
        }
        return navigation;

      } else {
        int lowerbound = (int) Math.ceil(((double) numberpagination - 1.0) / 2.0);
        int upperbound = (int) Math.floor(((double) numberpagination - 1.0) / 2.0);
        int up = Math.min(page + upperbound, maxpages);
        int down = Math.max(page - lowerbound, 1);
        for (int i = down; i <= up; i++) {
          navigation.add(String.valueOf(i));
        }
      }
    }
    navigation.add("next");

    return navigation;
  }

}
