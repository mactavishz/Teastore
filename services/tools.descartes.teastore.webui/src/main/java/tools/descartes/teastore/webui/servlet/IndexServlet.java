/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Icon;
import tools.descartes.teastore.entities.ImageSizePreset;
import tools.descartes.teastore.apis.ImageService;
import tools.descartes.teastore.apis.CategoryService;

/**
 * Servlet implementation for the web view of "Index".
 *
 * @author Andre Bauer
 */
@WebServlet("/index")
public class IndexServlet extends AbstractUIServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexServlet() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // checkforCookie(request, response);
        request.setAttribute("title", "TeaStore Home");
        // request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));

        // Map<String, String> envVars = System.getenv();
        // Iterate over the map and print each variable and its value
        // for (Map.Entry<String, String> entry : envVars.entrySet()) {
        //     System.out.println(entry.getKey() + "=" + entry.getValue());
        // }
        Icon reqestedIcon = new Icon(ImageSizePreset.ICON.getSize().toString());
        reqestedIcon = getIcon(reqestedIcon);
        List<Category> categories = getCategories(-1, -1);
        request.setAttribute("storeIcon", reqestedIcon.icon);
        request.setAttribute("CategoryList", categories);
        request.getRequestDispatcher("WEB-INF/pages/index.jsp").forward(request, response);
    }

    protected Icon getIcon(Icon requestedIcon) {
        String customURIString = "http://image:8080/tools.descartes.teastore.image";
        try {
            ImageService imageService = RestClientBuilder.newBuilder()
                    .baseUri(new URI(customURIString))
                    .build(ImageService.class);
            return imageService.getWebImages(requestedIcon);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected List<Category> getCategories(int start, int limit) {
        String customURIString = "http://persistence:8080/tools.descartes.teastore.persistence";
        try {
            CategoryService categoryService = RestClientBuilder.newBuilder()
                    .baseUri(new URI(customURIString))
                    .build(CategoryService.class);
            return categoryService.getCategories(start, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
