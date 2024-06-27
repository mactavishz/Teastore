export default {
  /**
	 * Text for message cookie.
	 */
	MESSAGECOOKIE: "TeaStoreMessageCookie",
	/**
	 * Text for error message cookie.
	 */
	ERRORMESSAGECOOKIE: "TeaStoreErrorMessageCookie",
	/**
	 * Text for successful login. 
	 */
	SUCESSLOGIN: "You are logged in!",
	/**
	 * Text for logout.
	 */
	SUCESSLOGOUT: "You are logged out!",
	/**
	 * Text for unauthorized access.
	 */
	UNAUTHORIZED: "You are not authorized! Please login first!",
	/**
	 * Text for wrong credentials.
	 */
	WRONGCREDENTIALS: "You used wrong credentials!",
	/**
	 * Text for number products cookie.
	 */
	PAGESIZECOOKIE: "TeaStorePageSize",
	/**
	 * Text for session blob.
	 */
	SESSIONBLOBCOOKIE: "SessionBlob",
	/**
	 * Text for confirmed order.
	 */
	ORDERCONFIRMED: "Your order is confirmed!",
	/**
	 * Text for updated cart.
	 */
	CARTUPDATED: "Your cart is updated!",
	/**
	 * Text for added product.
	 */
	ADDPRODUCT: (id: string) => `Product ${id} is added to cart!`,
	/**
	 * Text for removed product.
	 */
	REMOVEPRODUCT: (id: string) => `Product ${id} is removed from cart!`,
}