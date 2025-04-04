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
package tools.descartes.teastore.utils;


/**
 * Exception thrown if a 404 response was received.
 * @author Joakim von Kistowski
 *
 */
public class NotFoundException extends RuntimeException {


	/**
	 * The corresponding HTTP error code.
	 */
	public static final int ERROR_CODE = 404;
	
	private static final long serialVersionUID = -6617660221762786650L;

	/**
	 * Creates a new NotFoundException.
	 */
	public NotFoundException() {
		super();
	}
	
}
