import { OrderType } from "~/types";
export default class Order implements OrderType {
  id: number;
  userId: number;
  time: string | null;
  totalPriceInCents: number;
  addressName: string | null;
  address1: string | null;
  address2: string | null;

  creditCardCompany: string | null;
  creditCardNumber: string | null;
  creditCardExpiryDate: string | null;

  constructor() {
    this.id = 0;
    this.userId = 0;
    this.time = null;
    this.totalPriceInCents = 0;
    this.addressName = null;
    this.address1 = null;
    this.address2 = null;
    this.creditCardCompany = null;
    this.creditCardNumber = null;
    this.creditCardExpiryDate = null;
  }

  static copy(order: Order) {
    const newOrder = new Order();
    newOrder.setId(order.getId());
    newOrder.setUserId(order.getUserId());
    newOrder.setTime(order.getTime());
    newOrder.setTotalPriceInCents(order.getTotalPriceInCents());
    newOrder.setAddressName(order.getAddressName());
    newOrder.setAddress1(order.getAddress1());
    newOrder.setAddress2(order.getAddress2());
    newOrder.setCreditCardCompany(order.getCreditCardCompany());
    newOrder.setCreditCardNumber(order.getCreditCardNumber());
    newOrder.setCreditCardExpiryDate(order.getCreditCardExpiryDate());
    return newOrder;
  }

  getId() {
    return this.id;
  }

  setId(id: number) {
    this.id = id;
  }

  getUserId() {
    return this.userId;
  }

  setUserId(userId: number) {
    this.userId = userId;
  }

  getTime() {
    return this.time;
  }

  setTime(time: string | null) {
    this.time = time;
  }

  getTotalPriceInCents() {
    return this.totalPriceInCents;
  }

  setTotalPriceInCents(totalPriceInCents: number) {
    this.totalPriceInCents = totalPriceInCents;
  }

  getAddressName() {
    return this.addressName;
  }

  setAddressName(addressName: string | null) {
    this.addressName = addressName;
  }

  getAddress1() {
    return this.address1;
  }

  setAddress1(address1: string | null) {
    this.address1 = address1;
  }

  getAddress2() {
    return this.address2;
  }

  setAddress2(address2: string | null) {
    this.address2 = address2;
  }

  getCreditCardCompany() {
    return this.creditCardCompany;
  }

  setCreditCardCompany(creditCardCompany: string | null) {
    this.creditCardCompany = creditCardCompany;
  }

  getCreditCardNumber() {
    return this.creditCardNumber;
  }

  setCreditCardNumber(creditCardNumber: string | null) {
    this.creditCardNumber = creditCardNumber;
  }

  getCreditCardExpiryDate() {
    return this.creditCardExpiryDate;
  }

  setCreditCardExpiryDate(creditCardExpiryDate: string | null) {
    this.creditCardExpiryDate = creditCardExpiryDate;
  }

  hashCode() {
    const prime = 31;
    let result = 1;
    result = prime * result + (this.id ^ (this.id >>> 32));
    result = prime * result + (this.userId ^ (this.userId >>> 32));
    return result;
  }

  static equals(obj1: Order | null, obj2: Order | null) {
    if (obj1 === obj2) {
      return true;
    }
    if (obj1 === null || !(obj2 instanceof Order)) {
      return false;
    }
    return obj1.id === obj2.id && obj1.userId === obj2.userId;
  }
}