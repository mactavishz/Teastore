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

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import tools.descartes.teastore.model.domain.OrderItemRepository;
import tools.descartes.teastore.utils.AbstractCRUDEndpoint;
import tools.descartes.teastore.entities.OrderItem;
import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 * Persistence endpoint for for CRUD operations on orders.
 * @author Joakim von Kistowski
 *
 */
@Path("orderitems")
public class OrderItemEndpoint extends AbstractCRUDEndpoint<OrderItem> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long createEntity(final OrderItem orderItem) {
		return OrderItemRepository.REPOSITORY.createEntity(orderItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OrderItem findEntityById(final long id) {
		OrderItem item = OrderItemRepository.REPOSITORY.getEntity(id);
		if (item == null) {
			return null;
		}
		return new OrderItem(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<OrderItem> listAllEntities(final int startIndex, final int maxResultCount) {
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (OrderItem oi : OrderItemRepository.REPOSITORY.getAllEntities(startIndex, maxResultCount)) {
			orderItems.add(new OrderItem(oi));
		}
		return orderItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean updateEntity(long id, OrderItem orderItem) {
		return OrderItemRepository.REPOSITORY.updateEntity(id, orderItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean deleteEntity(long id) {
		return OrderItemRepository.REPOSITORY.removeEntity(id);
	}
	
	/**
	 * Returns all order items with the given product Id (all order items for that product).
	 * @param productId The id of the product.
	 * @param startPosition The index (NOT ID) of the first order item with the product to return.
	 * @param maxResult The max number of order items to return.
	 * @return list of order items with the product.
	 */
	@GET
	@Path("product/{product:[0-9][0-9]*}")
	@Timed(
			name = "getOrderItemsForProduct",
			absolute = true,
			tags = {"method=get", "url=/orderitems/product/{product}"},
			description = "A measure of how long it takes to get all order items for a product."
	)
	public List<OrderItem> listAllForProduct(@PathParam("product") final Long productId,
			@QueryParam("start") final Integer startPosition,
			@QueryParam("max") final Integer maxResult) {
		if (productId == null) {
			return listAll(startPosition, maxResult);
		}
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (OrderItem oi : OrderItemRepository.REPOSITORY.getAllEntitiesWithProduct(productId,
				parseIntQueryParam(startPosition), parseIntQueryParam(maxResult))) {
			orderItems.add(new OrderItem(oi));
		}
		return orderItems;
	}
	
	/**
	 * Returns all order items with the given order Id (all order items for that order).
	 * @param orderId The id of the product.
	 * @param startPosition The index (NOT ID) of the first order item with the product to return.
	 * @param maxResult The max number of order items to return.
	 * @return list of order items with the product.
	 */
	@GET
	@Path("order/{order:[0-9][0-9]*}")
	@Timed(
			name = "getOrderItemsForOrder",
			absolute = true,
			tags = {"method=get", "url=/orderitems/order/{order}"},
			description = "A measure of how long it takes to get all order items for an order."
	)
	public List<OrderItem> listAllForOrder(@PathParam("order") final Long orderId,
			@QueryParam("start") final Integer startPosition,
			@QueryParam("max") final Integer maxResult) {
		if (orderId == null) {
			return listAll(startPosition, maxResult);
		}
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (OrderItem oi : OrderItemRepository.REPOSITORY.getAllEntitiesWithOrder(orderId,
				parseIntQueryParam(startPosition), parseIntQueryParam(maxResult))) {
			orderItems.add(new OrderItem(oi));
		}
		return orderItems;
	}
}
