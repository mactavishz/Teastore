package tools.descartes.teastore.utils;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.Product;

/**
 * Utilities.
 * @author Simon
 *
 */
public final class RESTUtil {

  /**
   * hides constructor.
   */
  private void RestUtil() {

  }

  /**
   * Throw common exceptions.
   * @param responseWithStatus response
   * @throws NotFoundException error 404
   */
	public static void throwCommonExceptions(Response responseWithStatus)
			throws NotFoundException {
		if (responseWithStatus.getStatus() == Status.NOT_FOUND.getStatusCode()) {
			throw new NotFoundException();
		}
	}

	/**
	 * Read entity or return null-.
	 * @param r external call response
	 * @param entityClass class of object to load
   * @param <T> class of object to be loaded
	 * @return entity or null
	 */
	public static <T> T readEntityOrNull(Response r, Class<T> entityClass) {
		if (r != null) {
			if (r.getStatus() == 200) {
				return r.readEntity(entityClass);
			} else {
				r.bufferEntity();
			}
		}
		return null;
	}

	/**
	 * reads entity, throws potential errors and closes the response.
	 * @param responseWithStatus response
	 * @param entityClass class of object to be loaded
	 * @param <T> class of object to be loaded
	 * @return entity
	 */
	public static <T> T readThrowAndOrClose(Response responseWithStatus, Class<T> entityClass) {
		T entity = null;
		entity = readEntityOrNull(responseWithStatus, entityClass);
		throwCommonExceptions(responseWithStatus);
		return entity;
	}

  /**
   * Special case for orders.
   * @param r response
   * @return List of orders
   */
	public static List<Order> readListThrowAndOrCloseOrder(Response r) {
		List<Order> entity = null;
		if (r != null) {
			if (r.getStatus() == 200) {
				entity = r.readEntity(new GenericType<List<Order>>() { });
			} else {
				r.bufferEntity();
			}
		}
		if (r == null || entity == null) {
			entity = new ArrayList<Order>();
		}
		throwCommonExceptions(r);
		return entity;
	}

	/**
	 * Special case for products.
	 * @param r response
	 * @return List of products
	 */
	public static List<Product> readListThrowAndOrCloseProduct(Response r) {
		List<Product> entity = null;
		if (r != null) {
			if (r.getStatus() == 200) {
				entity = r.readEntity(new GenericType<List<Product>>() { });
			} else {
				r.bufferEntity();
			}
		}
		if (r == null || entity == null) {
			entity = new ArrayList<Product>();
		}
		throwCommonExceptions(r);
		return entity;
	}
}

