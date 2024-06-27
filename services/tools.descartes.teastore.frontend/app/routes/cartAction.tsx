import { redirect, ActionFunction, ActionFunctionArgs } from "@remix-run/node";
import { createPOSTFetcher } from "~/.server/request"
import { SessionBlobType } from "~/types";
import SessionBlob from "~/model/SessionBlob";
import { sessionBlobCookie, errorMessageCookie, messageCookie, getSessionBlob } from "~/.server/cookie";
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
    
    }
  } catch (err) {
    console.error(err)
    throw new Response("An error occurred", { status: 500 });
  }
}