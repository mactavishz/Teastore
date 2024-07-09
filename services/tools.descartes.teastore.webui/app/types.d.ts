import React from "react";

export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface Product {
  id: number;
  categoryId: number;
  name: string;
  description: string;
  listPriceInCents: number;
  image?: string;
}

export interface GlobalStateContextType {
  categoryList: Category[];
  message: string | null;
  errorMessage: string | null;
  setMessage: React.Dispatch<React.SetStateAction<string | null>>;
  setErrorMessage: React.Dispatch<React.SetStateAction<string | null>>;
  isLoggedIn: boolean;
  baseURL: string;
}

export interface OrderType {
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
}

export interface OrderItemType {
  id: number;
  productId: number;
  orderId: number;
  quantity: number;
  unitPriceInCents: number;
}

export interface SessionBlobType {
  uid: number | null;
  sid: string | null;
  token: string | null;
  message: string | null;
  order: OrderType;
  orderItems: OrderItemType[];
}