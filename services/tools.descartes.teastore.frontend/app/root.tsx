import {
  Links,
  Meta,
  Outlet,
  Scripts,
  useLoaderData,
} from "@remix-run/react";
import { useState } from "react";
import type { Category, IconData } from "./types";
import { LinksFunction, LoaderFunction, json, LoaderFunctionArgs } from "@remix-run/node";
import Header from "./components/header";
import Footer from "./components/footer";
import { useLayoutEffect } from "~/hooks/useLayoutEffect"; 
import { createGETFetcher, createPOSTFetcher } from "~/.server/request";
import { useLoginStatus } from "./.server/loginUtil";
import { GlobalStateContext } from '~/context/GlobalStateContext';
import { sessionBlobCookie, errorMessageCookie, messageCookie } from "~/.server/cookie";
import SessionBlob  from "~/model/SessionBlob"

export const links: LinksFunction = () => [
  {
    rel: "stylesheet",
    type: "text/css",
    href: "https://cdn.jsdelivr.net/npm/bootstrap3@3.3.5/dist/css/bootstrap.min.css"
  },
  {
    rel: "stylesheet",
    href: "/teastore.css", // Assuming your CSS file is named styles.css in the public folder
    type: "text/css",
    media: "screen",
  },
];

async function getIcon(): Promise<Response> {
  const response = await createPOSTFetcher("image", "image/getWebImages", {
    icon: "64x64"
  });
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getCategoryList(): Promise<Response> {
  const response = await createGETFetcher("persistence", "categories", {
    start: -1,
    max: -1
  });
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  let message: string | null = null;
  let errorMessage: string | null = null;
  let sessionBlob: SessionBlob;
  let categoryList: Category[] = [];
  try {
    const cookieHeader = request.headers.get("Cookie");
    sessionBlob = await sessionBlobCookie.parse(cookieHeader) || new SessionBlob();
    errorMessage = await errorMessageCookie.parse(cookieHeader);
    message = await messageCookie.parse(cookieHeader);
    const [iconRes, categoryListRes, loginStatus] = await Promise.all([getIcon(), getCategoryList(), useLoginStatus({ request })]);
    const iconData: IconData = await iconRes.json();
    categoryList = await categoryListRes.json() || [];
    return json({ icon: iconData.icon, loginStatus, categoryList, message, errorMessage, baseURL: process.env.BASE_URL }, {
      headers: [
        ["Set-Cookie", await errorMessageCookie.serialize(errorMessage, { maxAge: 0})],
        ["Set-Cookie", await messageCookie.serialize(message, { maxAge: 0 })]
      ]
    });
  } catch (error) {
    console.error('Loader error:', error);
    throw new Response("An error occurred", { status: 500 });
  }
}

export default function App() {
  const { icon, loginStatus, categoryList, errorMessage: defaultErrorMessage, message: defaultMessage, baseURL } = useLoaderData<typeof loader>();
  const [message, setMessage] = useState(defaultMessage)
  const [errorMessage, setErrorMessage] = useState(defaultErrorMessage)
  useLayoutEffect();
  return (
    <html lang="en">
      <head>
        <meta charSet="utf-8" />
        <meta
          name="viewport"
          content="width=device-width, initial-scale=1"
        />
        <meta httpEquiv="X-UA-Compatible" content="IE=edge" />
        <meta name="description" content="Welcome to TeaStore !" />
        <Meta />
        <Links />
      </head>
      <body suppressHydrationWarning={true}>
        <GlobalStateContext.Provider value={{ baseURL, categoryList, message, errorMessage, setMessage, setErrorMessage, isLoggedIn: loginStatus }}>
          <Header
            storeIcon={icon}
            login={loginStatus}
            message={message}
            errorMessage={errorMessage}
            setMessage={setMessage}
            setErrorMessage={setErrorMessage}
          />
          <Outlet />
          <Footer></Footer>
        </GlobalStateContext.Provider>
        <Scripts />
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap3@3.3.5/dist/js/bootstrap.min.js" />
      </body>
    </html>
  );
}