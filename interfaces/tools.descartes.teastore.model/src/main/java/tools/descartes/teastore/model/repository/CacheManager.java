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
package tools.descartes.teastore.model.repository;

import java.util.ArrayList;
import java.util.List;

import tools.descartes.teastore.model.domain.CategoryRepository;
import tools.descartes.teastore.model.restclient.PersistenceClient;


/**
 * Class for managing (mostly clearing) the persistence cache.
 * Sends cache clears to other persistence contexts for cache coherence.
 * @author Joakim von Kistowski
 *
 */
public final class CacheManager {
	/**
	 * The cache manager singleton.
	 */
	public static final CacheManager MANAGER = new CacheManager();

	private final PersistenceClient persistenceClient = new PersistenceClient();

	private CacheManager() {
		
	}
	
	/**
	 * Clears the entire cache in all persistence services,
	 * including this one.
	 */
	public void clearAllCaches() {
		CategoryRepository.REPOSITORY.getEMF().getCache().evictAll();
		try {
			persistenceClient.clearRemoteCacheREST(null);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Clears the cache for the entity class in all
	 * persistence services, including this one.
	 * @param entityClass The class to clear.
	 */
	public void clearCache(Class<?> entityClass) {
		clearLocalCacheOnly(entityClass);
		clearRemoteCache(entityClass);
	}
	
	/**
	 * Clears the cache in all remote persistence services
	 * (all, except the calling one).
	 * 
	 * Example usage scenario: Update on entity is automatically
	 * cached in local cache but remains unknown to remote services.
	 * @param entityClass The class to clear.
	 * @return List of all responses. Contain the class names or "null" if errors occured.
	 */
	public List<String> clearRemoteCache(Class<?> entityClass) {
		List<String> responses = null;
		try {
			String result = persistenceClient.clearRemoteCacheREST(entityClass);
			if (result != null) {
				responses = new ArrayList<>();
				responses.add(result);
			}
		} catch (Exception e) {
			
		}
		return responses;
	}
	
	/**
	 * Clear only the local Cache for the entity class in question.
	 * @param entityClass The class to clear.
	 */
	public void clearLocalCacheOnly(Class<?> entityClass) {
		CategoryRepository.REPOSITORY.getEMF().getCache().evict(entityClass);
	}
	
	/**
	 * Clear only the entire local Cache for all classes.
	 */
	public void clearLocalCacheOnly() {
		CategoryRepository.REPOSITORY.getEMF().getCache().evictAll();
	}
	
	/**
	 * Reset the local and all remote EMFs.
	 * @return List of all responses. Contain the "clearedEMF", or "null" if errors occured.
	 */
	public List<String> resetAllEMFs() {
		resetLocalEMF();
		return resetRemoteEMFs();
	}
	
	/**
	 * Reset all remote EMFs.
	 * @return List of all responses. Contain the "clearedEMF", or "null" if errors occured.
	 */
	public List<String> resetRemoteEMFs() {
		List<String> responses = null;
		try {
			String result = persistenceClient.resetRemoteEMF();
			if (result != null) {
				responses = new ArrayList<>();
				responses.add(result);
			}
		} catch (Exception e) {
		}
		return responses;
	}
	
	/**
	 * Reset the local EMF.
	 */
	public void resetLocalEMF() {
		EMFManager.clearEMF();
	}
}
