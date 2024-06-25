import type { MetaFunction } from "@remix-run/node";
import CategoryList from "~/components/categoryList";
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Home" },
    { name: "description", content: "Welcome to TeaStore !" },
    { name: "viewport", content: "width=device-width, initial-scale=1" },
    { name: "http-equiv", content: "X-UA-Compatible", "value": "IE=edge" },
    { name: "charset", content: "utf-8" },
  ];
};

export default function Index() {
  const { categoryList } = useContext(GlobalStateContext);

  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} /> 
        <div id="MainImage" className="col-sm-6 col-lg-8">
          <img className="titleimage" src="/images/front.png" />
        </div>
      </div>
    </div>
  );
}
