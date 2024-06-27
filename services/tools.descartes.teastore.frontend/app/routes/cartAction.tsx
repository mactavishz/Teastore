import { redirect, ActionFunction, ActionFunctionArgs } from "@remix-run/node";
import { createPOSTFetcher, createPutFetcher } from "~/.server/request"
import { SessionBlobType, OrderItemType } from "~/types";
import SessionBlob from "~/model/SessionBlob";
import { sessionBlobCookie, errorMessageCookie, messageCookie, getSessionBlob } from "~/.server/cookie";
import { useLoginStatus  } from "~/.server/loginUtil"
import appConfig from "~/appConfig";
import { parse, setDate, format, isValid } from 'date-fns';

function parseAndFormatCreditCardDate(date: string) {
  const parseFormat = 'MM/yyyy';
  const outputFormat = 'yyyy-MM-dd';
  try {
    const parsedDate = parse(date, parseFormat, new Date());
    
    if (!isValid(parsedDate)) {
      throw new Error('Invalid date');
    }
    const firstDayOfMonth = setDate(parsedDate, 1);
    return format(firstDayOfMonth, outputFormat);
  } catch (error) {
    console.error('Error parsing date:', error)
    return null;
  }
}

async function addProductToCart(blob: SessionBlobType, productId: string): Promise<Response> {
  const response = await createPOSTFetcher("auth", `cart/add/${productId}`, blob);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response 
}

async function removeProductFromCart(blob: SessionBlobType, productId: string): Promise<Response> {
  const response = await createPOSTFetcher("auth", `cart/remove/${productId}`, blob);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response 
}

async function updateOrderItemQuantity(blob: SessionBlobType, productId: string | number, quantity: number): Promise<Response> {
  const response = await createPutFetcher("auth", `cart/${productId}?quantity=${quantity}`, blob);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response 
}

async function confirmOrder(formData: FormData, blob: SessionBlobType): Promise<SessionBlobType> {
  const params: string[] = ["firstname", "lastname", "address1", "address2", "cardtype", "cardnumber", "expirydate"]
  let data: string[] = [];
  for (const param of params) {
    const val = formData.get(param);
    if (!val) {
      break;
    }
    data.push(val.toString());
  }
  if (data.length != params.length) {
    throw new Error("Invalid form data");
  }
  let price = 0;
  for (const orderItem of blob.orderItems) {
    price += orderItem.quantity * orderItem.unitPriceInCents;
  }
  data.push(price.toString());
  const res = await placeOrder(blob, data);
  return res.json();
}

async function placeOrder(blob: SessionBlobType, data: string[]): Promise<Response> {
  const addressName = `${data[0]} ${data[1]}`
  const address1 = data[2];
  const address2 = data[3];
  const creditCardCompany = data[4];
  const creditCardNumber = data[5];
  const creditCardExpiryDate = parseAndFormatCreditCardDate(data[6]);
  const totalPriceInCents = data[7];
  const query = `addressName=${addressName}&address1=${address1}&address2=${address2}&creditCardCompany=${creditCardCompany}&creditCardNumber=${creditCardNumber}&creditCardExpiryDate=${creditCardExpiryDate}&totalPriceInCents=${totalPriceInCents}`
  const response = await createPOSTFetcher("auth", `useractions/placeorder?${query}`, blob);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response
}

async function updateCartQuantities(formData: FormData, blob: SessionBlobType, orderItems: OrderItemType[]): Promise<SessionBlobType> {
  for (const orderItem of orderItems) {
    if (formData.has(`orderitem_${orderItem.productId}`)) {
      const res = await updateOrderItemQuantity(blob, orderItem.productId, parseInt(formData.get(`orderitem_${orderItem.productId}`)?.toString() || "1"));
      blob = await res.json();
    }
  }
  return blob;
}

export const action: ActionFunction = async ({ request, params }: ActionFunctionArgs) => {
  try {
    const formData = await request.formData();
    const dataKeys = Array.from(formData.keys());
    const data = Object.fromEntries(formData.entries());
    if (data[`addToCart`]) {
      const productId: string = data.productid.toString(); 
      const sessionBlob: SessionBlobType = await getSessionBlob(request) || new SessionBlob();
      const res = await addProductToCart(sessionBlob, productId);
      const newSessionBlob: SessionBlobType = await res.json();
      return redirect("/cart", {
        headers: [
          ["Set-Cookie", await sessionBlobCookie.serialize(newSessionBlob)]
        ]
      }) 
    } else if (
      dataKeys.findIndex((key) => key.startsWith("removeProduct_")) !== -1
    ) {
      const productId: string = dataKeys.find((key) => key.startsWith("removeProduct_"))?.split("_")[1] || "";
      if (!productId) {
        throw new Error("Invalid product id");
      }
      const sessionBlob: SessionBlobType = await getSessionBlob(request) || new SessionBlob();
      const res = await removeProductFromCart(sessionBlob, productId);
      const newSessionBlob: SessionBlobType = await res.json();
      return redirect("/cart", {
        headers: [
          ["Set-Cookie", await sessionBlobCookie.serialize(newSessionBlob)],
          ["Set-Cookie", await messageCookie.serialize(appConfig.REMOVEPRODUCT(productId))]
        ]
      }) 
    
    } else if (data[`updateCartQuantities`]) {
      let sessionBlob: SessionBlobType = await getSessionBlob(request) || new SessionBlob();
      const orderItems = sessionBlob?.orderItems || [];
      sessionBlob = await updateCartQuantities(formData, sessionBlob, orderItems); 
      return redirect("/cart", {
        headers: [
          ["Set-Cookie", await messageCookie.serialize(appConfig.CARTUPDATED)],
          ["Set-Cookie", await sessionBlobCookie.serialize(sessionBlob)]
        ]
      })
    } else if (data[`proceedtoCheckout`]) {
      const loginStatus = await useLoginStatus({ request });
      if (!loginStatus) {
        return redirect("/login", {
          headers: [
            ["Set-Cookie", await errorMessageCookie.serialize(appConfig.UNAUTHORIZED)]
          ]
        })
      } else {
        let sessionBlob: SessionBlobType = await getSessionBlob(request) || new SessionBlob();
        const orderItems = sessionBlob?.orderItems || [];
        sessionBlob = await updateCartQuantities(formData, sessionBlob, orderItems); 
        return redirect("/order", {
          headers: [
            ["Set-Cookie", await sessionBlobCookie.serialize(sessionBlob)]
          ]
        })
      }
    } else if (data[`confirm`]) {
      try {
        let sessionBlob: SessionBlobType = await getSessionBlob(request);
        sessionBlob = await confirmOrder(formData, sessionBlob);
        return redirect("/", {
          headers: [
            ["Set-Cookie", await messageCookie.serialize(appConfig.ORDERCONFIRMED)],
            ["Set-Cookie", await sessionBlobCookie.serialize(sessionBlob)]
          ]
        })
      } catch (err) {
        console.error(err)
        return redirect("/order", {
          headers: [
            ["Set-Cookie", await errorMessageCookie.serialize(appConfig.ORDERFAILED)]
          ]
        })
      }
    }
  } catch (err) {
    console.error(err)
    throw new Response("An error occurred", { status: 500 });
  }
}