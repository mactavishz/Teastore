import type { MetaFunction } from "@remix-run/node";
import { LoaderFunction, json } from "@remix-run/node";
import CategoryList from "~/components/categoryList";
import { createGETFetcher } from "~/.server/request";
import { useLoaderData } from "@remix-run/react";

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Home" },
    { name: "description", content: "Welcome to TeaStore !" },
    { name: "viewport", content: "width=device-width, initial-scale=1" },
    { name: "http-equiv", content: "X-UA-Compatible", "value": "IE=edge" },
    { name: "charset", content: "utf-8" },
  ];
};

export const loader: LoaderFunction = async () => {
  try {
    const categoryListRes = await getCategoryList(); 
    const categoryList = await categoryListRes.json();
    console.log(categoryList)
    return json({ categoryList });
  } catch (error) {
    console.error('Loader error:', error);
    throw new Response("An error occurred", { status: 500 });
  }
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


export default function Index() {
  const { categoryList } = useLoaderData<typeof loader>();
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
