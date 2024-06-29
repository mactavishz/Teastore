import { OrderItemType } from "~/types";

export class OrderItem implements OrderItemType {
  id: number;
  productId: number;
  orderId: number;
  quantity: number;
  unitPriceInCents: number;

  constructor() {
    this.id = 0;
    this.productId = 0;
    this.orderId = 0;
    this.quantity = 0;
    this.unitPriceInCents = 0;
  }

  static copy(orderItem: OrderItem) {
    const newOrderItem = new OrderItem();
    newOrderItem.setId(orderItem.getId());
    newOrderItem.setProductId(orderItem.getProductId());
    newOrderItem.setOrderId(orderItem.getOrderId());
    newOrderItem.setQuantity(orderItem.getQuantity());
    newOrderItem.setUnitPriceInCents(orderItem.getUnitPriceInCents());
    return newOrderItem;
  }

  getId() {
    return this.id;
  }

  setId(id: number) {
    this.id = id;
  }

  getProductId() {
    return this.productId;
  }

  setProductId(productId: number) {
    this.productId = productId;
  }

  getOrderId() {
    return this.orderId;
  }

  setOrderId(orderId: number) {
    this.orderId = orderId;
  }

  getQuantity() {
    return this.quantity;
  }

  setQuantity(quantity: number) {
    this.quantity = quantity;
  }

  getUnitPriceInCents() {
    return this.unitPriceInCents;
  }

  setUnitPriceInCents(unitPriceInCents: number) {
    this.unitPriceInCents = unitPriceInCents;
  }

  hashCode() {
    const prime = 31;
    let result = 1;
    result = prime * result + (this.id ^ (this.id >>> 32));
    result = prime * result + (this.orderId ^ (this.orderId >>> 32));
    result = prime * result + (this.productId ^ (this.productId >>> 32));
    return result;
  }

  static equals(obj1: OrderItem | null, obj2: OrderItem | null) {
    if (obj1 === obj2) {
      return true;
    }
    if (obj1 === null || !(obj2 instanceof OrderItem)) {
      return false;
    }
    return obj1.id === obj2.id && obj1.orderId === obj2.orderId && obj1.productId === obj2.productId;
  }
}