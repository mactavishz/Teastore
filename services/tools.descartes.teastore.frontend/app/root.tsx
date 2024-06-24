import {
  Links,
  Meta,
  Outlet,
  Scripts
} from "@remix-run/react";
import type { LinksFunction } from "@remix-run/node";
import Header from "./components/header";
import Footer from "./components/footer";
import { useLayoutEffect } from "~/hooks/useLayoutEffect"; // Adjust the import path as needed

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

export default function App() {
  useLayoutEffect();

  return (
    <html lang="en">
      <head>
        <Meta />
        <Links />
      </head>
      <body>
        <Header storeIcon={""} login={false} message={""} errormessage={""} />
        <Outlet />
        <Footer></Footer>
        <Scripts />
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap3@3.3.5/dist/js/bootstrap.min.js" />
      </body>
    </html>
  );
}