import { isRouteErrorResponse, useRouteError } from "@remix-run/react";
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";
import { getURL } from "~/utils/url";
import CategoryList from "~/components/categoryList";

export default function ErrorMessage({ errorMessage = null }: { errorMessage?: string | null }) {
  const error = useRouteError();
  const errorTitle = isRouteErrorResponse(error) ? `${error.status} ${error.statusText}` : error instanceof Error ? error.message : (errorMessage || "Oops, something went wrong!");
  const { categoryList } = useContext(GlobalStateContext);
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-sm-3 col-md-3 col-lg-2 sidebar"></div>
        <div id="MainImage" className="col-sm-6 col-lg-8">
          <h2>
            {errorTitle}
          </h2>
          <img className="titleimage" src="/images/error.png" />
          <div className="row">
            <a type="button" className="btn errorbtn" href={getURL("/")}>Back to Shop</a>
            <a type="button" className="btn errorbtn" href={getURL("/status")}>Check Status</a>
          </div>
        </div>
      </div>
    </div>
  )
}