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
import tools.descartes.teastore.utils.Service;
import tools.descartes.teastore.webui.restclient.HTTPClient;

/**
 * Servlet implementation for the web view of "About us".
 * 
 * @author Andre Bauer
 */
@WebServlet("/about")
public class AboutUsServlet extends AbstractUIServlet {
  private static final long serialVersionUID = 1L;
  /**
   * @see HttpServlet#HttpServlet()
   */
  public AboutUsServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    checkforCookie(request, response);
    String WebUI = Service.WEBUI.getServiceName();
    request.setAttribute("portraitAndre", String.format("/%s/images/andreBauer.png", WebUI));
    request.setAttribute("portraitJohannes",String.format("/%s/images/johannesGrohmann.png", WebUI));
    request.setAttribute("portraitJoakim", String.format("/%s/images/joakimKistowski.png", WebUI));
    request.setAttribute("portraitSimon",String.format("/%s/images/simonEismann.png", WebUI));
    request.setAttribute("portraitNorbert", String.format("/%s/images/norbertSchmitt.png", WebUI));
    request.setAttribute("portraitKounev", String.format("/%s/images/samuelKounev.png", WebUI));
    request.setAttribute("descartesLogo", String.format("/%s/images/descartesLogo.png", WebUI));
    request.setAttribute("storeIcon", ICON_URL);
    request.setAttribute("title", "TeaStore About Us");
    request.setAttribute("login", isUserLoggedInLocal(request));
    request.getRequestDispatcher("pages/about.jsp").forward(request, response);
  }

}
