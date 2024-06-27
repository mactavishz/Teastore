import { redirect, ActionFunction, ActionFunctionArgs } from "@remix-run/node";
import { createPOSTFetcher, createPutFetcher } from "~/.server/request"
import { SessionBlobType, OrderItemType } from "~/types";
import SessionBlob from "~/model/SessionBlob";
import { sessionBlobCookie, errorMessageCookie, messageCookie, getSessionBlob } from "~/.server/cookie";
import { useLoginStatus  } from "~/.server/loginUtil"
import appConfig from "~/appConfig";

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
      // TODO: implement confirm order
    }
  } catch (err) {
    console.error(err)
    throw new Response("An error occurred", { status: 500 });
  }
}