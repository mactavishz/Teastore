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
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.webui.restclient.HTTPClient;

/**
 * Servlet implementation for the web view of "Profile".
 * 
 * @author Andre Bauer
 */
@WebServlet("/profile")
public class ProfileServlet extends AbstractUIServlet {

  private static final long serialVersionUID = 1L;
  /**
   * @see HttpServlet#HttpServlet()
   */
  public ProfileServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    checkforCookie(request, response);
    if (!HTTPClient.isLoggedIn(getSessionBlob(request))) {
      redirect("/", response);
    } else {

      request.setAttribute("storeIcon", String.format("/%s/images/icon.png", Service.WEBUI.getServiceName()));
      request.setAttribute("CategoryList", HTTPClient.getCategories(-1, -1));
      request.setAttribute("title", "TeaStore Home");
      request.setAttribute("User", HTTPClient.getUser(getSessionBlob(request).getUid()));
      request.setAttribute("Orders", HTTPClient.getUserOrders(getSessionBlob(request).getUid(), -1, -1));
      request.setAttribute("helper", ELHelperUtils.UTILS);
      request.setAttribute("login", HTTPClient.isLoggedIn(getSessionBlob(request)));
      request.getRequestDispatcher("WEB-INF/pages/profile.jsp").forward(request, response);
    }
  }

}
