import SessionBlob from "~/model/SessionBlob";
import { createPOSTFetcher } from "~/.server/request"
import { sessionBlobCookie } from "~/.server/cookie";

async function getLoginStatus(blob: SessionBlob): Promise<Response> {
  const response = await createPOSTFetcher("auth", "useractions/isloggedin", blob);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

export async function useLoginStatus({ request }: { request: Request }) {
  let sessionBlob: SessionBlob;
  let loginStatus: boolean = false;
  try {
    const cookieHeader = request.headers.get("Cookie");
    sessionBlob = await sessionBlobCookie.parse(cookieHeader) || new SessionBlob();
    const loginStatusRes = await getLoginStatus(sessionBlob);
    try {
      await loginStatusRes.json();
      loginStatus = true
    } catch (err) {
      loginStatus = false;
    }
  } catch (error) {
    console.error(error);
    loginStatus = false;
  }
  return loginStatus;
}