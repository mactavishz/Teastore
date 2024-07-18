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
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.descartes.teastore.webui.restclient.HTTPClient;
import tools.descartes.teastore.utils.Service;

/**
 * Servlet to show database and other service status.
 *
 * @author Joakim von Kistowski
 */
@WebServlet("/status")
public class StatusServlet extends AbstractUIServlet {
  private static final long serialVersionUID = 1L;
  private static final HTTPClient client = new HTTPClient();

  /**
   * @see HttpServlet#HttpServlet()
   */
  public StatusServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException{
    checkforCookie(request, response);
    request.setAttribute("storeIcon", String.format("/%s/images/icon.png", Service.WEBUI.getServiceName()));
    request.setAttribute("title", "TeaStore Status");
    request.setAttribute("webuiservers", getServer("WEBUI_HOST", "WEBUI_PORT"));
    request.setAttribute("authenticationservers", getServer("AUTH_HOST", "AUTH_PORT"));
    request.setAttribute("persistenceservers", getServer("PERSISTENCE_HOST", "PERSISTENCE_PORT"));
    request.setAttribute("imageservers", getServer("IMAGE_HOST", "IMAGE_PORT"));
    request.setAttribute("recommenderservers", getServer("RECOMMENDER_HOST", "RECOMMENDER_PORT"));
    request.getRequestDispatcher("WEB-INF/pages/status.jsp").forward(request, response);
  }

  private List<String> getServer(String host, String port) {
    List<String> servers = new ArrayList<>();
    try {
      String baseURL = Service.getServiceBaseURL(host, port);
      String serverName  = Service.getServerName(host, port);
      boolean isHealthy = client.isServiceUp(baseURL);
      if (isHealthy) {
        servers.add(serverName);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return servers;
  }
}
