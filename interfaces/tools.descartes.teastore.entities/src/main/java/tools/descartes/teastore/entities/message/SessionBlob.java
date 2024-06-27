package tools.descartes.teastore.entities.message;

import java.util.LinkedList;
import java.util.List;

import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Blob containing all information about the user session.
 * @author Simon
 */
public class SessionBlob {

	@JsonProperty("uid")
	private Long uid;
	@JsonProperty("sid")
	private String sid;
	@JsonProperty("token")
	private String token;
	@JsonProperty("order")
	private Order order;
	@JsonProperty("orderItems")
	private List<OrderItem> orderItems = new LinkedList<OrderItem>();
	@JsonProperty("message")
	private String message;
	
	/**
	 * Constructor, creates an empty order.
	 */
	public SessionBlob() {
		this.setOrder(new Order());
	}

	/**
	 * Getter for the userid.
	 * @return userid
	 */
	public Long getUid() {
		return uid;
	}

	/**
	 * Setter for the userid.
	 * @param uID userid
	 */
	public void setUid(Long uID) {
		uid = uID;
	}

	/**
	 * Getter for session id.
	 * @return session id
	 */
	public String getSid() {
		return sid;
	}

	/**
	 * Setter for session id.
	 * @param sID session id
	 */
	public void setSid(String sID) {
		sid = sID;
	}

	/**
	 * Getter for trust token.
	 * @return trust token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Setter for trust token.
	 * @param token trust token.
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * Setter for the message.
	 * @param message String
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Getter for the message.
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Getter for order.
	 * @return order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Setter for order.
	 * @param order order
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * Getter for order items.
	 * @return order items.
	 */
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	/**
	 * Setter for order items.
	 * @param orderItems list of order items
	 */
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
}
