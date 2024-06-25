import {
  Links,
  Meta,
  Outlet,
  Scripts,
  useLoaderData
} from "@remix-run/react";
import { LinksFunction, LoaderFunction, json } from "@remix-run/node";
import Header from "./components/header";
import Footer from "./components/footer";
import { useLayoutEffect } from "~/hooks/useLayoutEffect"; // Adjust the import path as needed
import { createGETFetcher, createPOSTFetcher } from "~/.server/request"; // Adjust the import path as needed

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

interface iconData {
  icon: string
}

async function getIcon(): Promise<Response> {
  const response = await createPOSTFetcher("image", "image/getWebImages", {
    icon: "64x64"
  });
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getLoginStatus(): Promise<Response> {
  const response = await createPOSTFetcher("auth", "useractions/isloggedin", {});
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

export const loader: LoaderFunction = async () => {
  try {
    const [iconRes, loginStatusRes] = await Promise.all([getIcon(), getLoginStatus()]);
    const iconData: iconData = await iconRes.json();
    const loginStatusText = await loginStatusRes.text();
    let loginStatus = false;
    if (!loginStatusText || loginStatusText.trim() === "") {
      loginStatus = false;
    } else {
      loginStatus = true
    }
    return json({ icon: iconData.icon, loginStatus });
  } catch (error) {
    console.error('Loader error:', error);
    throw new Response("An error occurred", { status: 500 });
  }
}

export default function App() {
  const { icon, loginStatus } = useLoaderData<typeof loader>();
  useLayoutEffect();
  return (
    <html lang="en">
      <head>
        <Meta />
        <Links />
      </head>
      <body>
        <Header storeIcon={icon} login={loginStatus} message={""} errormessage={""} />
        <Outlet />
        <Footer></Footer>
        <Scripts />
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap3@3.3.5/dist/js/bootstrap.min.js" />
      </body>
    </html>
  );
}