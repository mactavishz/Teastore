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
package tools.descartes.teastore.persistence.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;


import org.eclipse.microprofile.metrics.annotation.Timed;
import tools.descartes.teastore.model.repository.DataGeneratorUtil;

/**
 * Persistence endpoint for generating new database content.
 * @author Joakim von Kistowski
 *
 */
@Path("generatedb")
public class DatabaseGenerationEndpoint {
	/**
	 * Returns the is finished flag for database generation.
	 * Also returns false if the persistence provider is in maintenance mode.
	 * @return True, if generation is finished; false, if in progress.
	 */
	@GET
	@Path("finished")
	@Timed(name = "isFinished", tags = {"method=get", "url=/generatedb/finished"}, absolute = true, description = "Time and frequency to check if database generation is finished.")
	public Response isFinshed() {
		boolean finishedGenerating = false;
		boolean isDatebaseEmpty = DataGeneratorUtil.isDatabaseEmpty();
		if (isDatebaseEmpty) {
			finishedGenerating = true;
		} else {
			finishedGenerating = DataGeneratorUtil.getGenerationFinishedFlag();
		}
		return Response.serverError().entity(finishedGenerating).build();
	}
}
