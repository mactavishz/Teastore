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
import tools.descartes.teastore.webui.restclient.HTTPClient;
import tools.descartes.teastore.utils.Service;

/**
 * Servlet implementation for the web view of "Error page".
 * 
 * @author Andre Bauer
 */
@WebServlet("/error")
public class ErrorServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
	private static final HTTPClient client = new HTTPClient();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ErrorServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

		if (statusCode == null) {
			redirect("/", response);
		} else {
			request.setAttribute("CategoryList", client.getCategories(-1, -1));
			request.setAttribute("storeIcon", String.format("/%s/images/icon.png", Service.WEBUI.getServiceName()));
			request.setAttribute("errorImage", String.format("/%s/images/error.png", Service.WEBUI.getServiceName()));
			request.setAttribute("title", "TeaStore Error ");
			request.setAttribute("login", client.isLoggedIn(getSessionBlob(request)));
			request.getRequestDispatcher("WEB-INF/pages/error.jsp").forward(request, response);

		}
	}

}
