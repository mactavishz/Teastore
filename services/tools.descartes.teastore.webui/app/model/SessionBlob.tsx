import { SessionBlobType, OrderType, OrderItemType } from "~/types";
import Order from "./Order";

export default class SessionBlob implements SessionBlobType {
  uid: number | null;
  sid: string | null;
  token: string | null;
  message: string | null;
  order: OrderType;
  orderItems: OrderItemType[]

  constructor() {
    this.uid = null;
    this.sid = null;
    this.token = null;
    this.order = new Order();
    this.orderItems = [];
    this.message = null;
  }

  getUid() {
    return this.uid;
  }

  setUid(uid: number | null) {
    this.uid = uid;
  }

  getSid() {
    return this.sid;
  }

  setSid(sid: string | null) {
    this.sid = sid;
  }

  getToken() {
    return this.token;
  }

  setToken(token: string | null) {
    this.token = token;
  }

  setMessage(message: string | null) {
    this.message = message;
  }

  getMessage() {
    return this.message;
  }

  getOrder() {
    return this.order;
  }

  setOrder(order: Order) {
    this.order = order;
  }

  getOrderItems() {
    return this.orderItems;
  }

  setOrderItems(orderItems: OrderItemType[]) {
    this.orderItems = orderItems;
  }
}