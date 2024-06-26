import { redirect, ActionFunction, ActionFunctionArgs } from "@remix-run/node";
import { createPOSTFetcher } from "~/.server/request";
import SessionBlob from "~/model/SessionBlob";
import { sessionBlobCookie, errorMessageCookie, messageCookie } from "~/.server/cookie";
import appConfig from "~/appConfig";

async function login(blob: SessionBlob, username: string | null = "", password: string | null = ""): Promise<Response> {
  const response = await createPOSTFetcher("auth", `useractions/login?name=${username}&password=${password}`, blob);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function logout(blob: SessionBlob): Promise<Response> {
  const response = await createPOSTFetcher("auth", `useractions/logout`, blob);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

// this route is purely functional and doesn't need to be rendered
export const action: ActionFunction = async ({ request, params }: ActionFunctionArgs) => {
  const formData = await request.formData();
  const username = formData.get("username");
  const password = formData.get("password");
  const logoutField = formData.get("logout")
  const referer = request.headers.get("referer")
  let isloggedin = false;
  let sessionBlob = null;
 
  if (username && typeof username !== "string" || password && typeof password !== "string" || logoutField && typeof logoutField !== "string") {
    return { error: "Invalid form submission" };
  }
  
  try {
    const cookieHeader = request.headers.get("Cookie");
    sessionBlob = await sessionBlobCookie.parse(cookieHeader) || new SessionBlob();
  } catch (err) {
    console.error(err)
    sessionBlob = new SessionBlob();
  }
  
  if (logoutField) {
    try {
      const logoutRes = await logout(sessionBlob);
      const logoutData: SessionBlob = await logoutRes.json();
      sessionBlob = logoutData;
    } catch (err) {
      console.error(err)
    }
    return redirect(`/`, {
      headers: [
        ["Set-Cookie", await messageCookie.serialize(appConfig.SUCESSLOGOUT)],
        ["Set-Cookie", await sessionBlobCookie.serialize(sessionBlob, {
          maxAge: 0
        })]
      ]
    })
  }
  
  try {
    const loginRes = await login(new SessionBlob(), username, password);
    const loginData: SessionBlob = await loginRes.json();
    isloggedin = loginData != null && loginData.sid != null;
    if (isloggedin) {
      sessionBlob = loginData;
    }
  } catch (err) {
    console.error(err)
    isloggedin = false;
  }
  if (isloggedin) {
    if (referer?.endsWith("/cart")) {
      return redirect(`/cart`, {
        headers: [
          ["Set-Cookie", await messageCookie.serialize(appConfig.SUCESSLOGIN)],
          ["Set-Cookie", await sessionBlobCookie.serialize(sessionBlob)]
        ]
      });
    } else {
      return redirect(`/`, {
        headers: [
          ["Set-Cookie", await messageCookie.serialize(appConfig.SUCESSLOGIN)],
          ["Set-Cookie", await sessionBlobCookie.serialize(sessionBlob)]
        ]
      });
    }
  } else {
    return redirect(`/login`, {
      headers: [
        ["Set-Cookie", await errorMessageCookie.serialize(appConfig.WRONGCREDENTIALS)] 
      ]
    })
  }
}
