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
package tools.descartes.teastore.imagegenerator.storage.rules;

import java.util.function.Predicate;

import tools.descartes.teastore.imagegenerator.cache.entry.ICachable;

/**
 * Rule for all images.
 * @author Norbert
 *
 * @param <T> cachable class
 */
public class StoreAll<T extends ICachable<T>> implements Predicate<T> {

  @Override
  public boolean test(T t) {
    return true;
  }

}
