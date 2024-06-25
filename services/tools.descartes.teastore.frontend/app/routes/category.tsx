import {
  LoaderFunctionArgs,
  LoaderFunction,
  json,
} from "@remix-run/node";
import {
  useLoaderData
} from "@remix-run/react";
import type { Category, Product } from "~/types";
import ProductItem from "~/components/productItem";
import CategoryList from "~/components/categoryList";
import Pagination from "~/components/pagination";
import { createGETFetcher, createPOSTFetcher } from "~/.server/request"; // Adjust the import path as needed
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";

const INITIAL_PRODUCT_DISPLAY_COUNT = 20;
const PRODUCT_DISPLAY_COUNT_OPTIONS = [5, 10, 20, 30];

function createNavigation(products: number, page: number, numberProducts: number) {
  const navigation: string[] = [];
  const numberpagination = 5;

  const maxpages = Math.ceil(products / numberProducts);

  if (maxpages < page) {
    return navigation;
  }

  if (page === 1) {
    if (maxpages === 1) {
      navigation.push("1");
      return navigation;
    }
    const min = Math.min(maxpages, numberpagination + 1);
    for (let i = 1; i <= min; i++) {
      navigation.push(i.toString());
    }
  } else {
    navigation.push("previous");
    if (page === maxpages) {
      const max = Math.max(maxpages - numberpagination, 1);
      for (let i = max; i <= maxpages; i++) {
        navigation.push(i.toString());
      }
      return navigation;
    } else {
      const lowerbound = Math.ceil((numberpagination - 1) / 2);
      const upperbound = Math.floor((numberpagination - 1) / 2);
      const up = Math.min(page + upperbound, maxpages);
      const down = Math.max(page - lowerbound, 1);
      for (let i = down; i <= up; i++) {
        navigation.push(i.toString());
      }
    }
  }
  navigation.push("next");

  return navigation;
}

async function getCategory(id: string): Promise<Response> {
  const response = await createGETFetcher("persistence", `categories/${id}`);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getProductCountByCategory(id: string): Promise<Response> {
  const response = await createGETFetcher("persistence", `products/count/${id}`);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getProductListByCategory(id: string, page: number, numProducts: number): Promise<Response> {
  const response = await createGETFetcher("persistence", `products/category/${id}`, {
    start: (page - 1) * numProducts,
    max: numProducts
  });
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getProductImages(productIds: string[]): Promise<Response> {
  let imageMap = {};
  for (const productId of productIds) {
    Object.assign(imageMap, { [productId]: "64x64" })
  }
  const response = await createPOSTFetcher("image", "image/getProductImages", imageMap);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  try {
    const url = new URL(request.url);
    let categoryId = url.searchParams.get("category");
    let page = url.searchParams.get("page");

    if (!categoryId) {
      categoryId = "2";
    }
    if (!page) {
      page = "1";
    }

    const [categoryRes, productCountRes] = await Promise.all([getCategory(categoryId), getProductCountByCategory(categoryId)]);
    const categoryData: Category = await categoryRes.json();
    const productCount = parseInt(await productCountRes.text());
    const numProductsPerPage = INITIAL_PRODUCT_DISPLAY_COUNT;
    const maxPages = Math.ceil(productCount / numProductsPerPage);
    let pageNum = Math.min(parseInt(page), maxPages);
    const pagination = createNavigation(productCount, pageNum, INITIAL_PRODUCT_DISPLAY_COUNT);
    const productListRes = await getProductListByCategory(categoryId, pageNum, numProductsPerPage);
    const productList = await productListRes.json();
    const productIds = productList.map((product: Product) => product.id.toString());
    const imageRes = await getProductImages(productIds);
    const images = await imageRes.json();
    for (const product of productList) {
      product.image = images[product.id];
    }
    return json({ category: categoryData, productList, pagination, productCount, pageNum, numProductsPerPage });
  } catch (error) {
    console.error('Loader error:', error);
    throw new Response("An error occurred", { status: 500 });
  }
}

export default function CategoryPage() {
  const { category, productList, pagination, productCount, pageNum } = useLoaderData<typeof loader>();
  const { categoryList } = useContext(GlobalStateContext);
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-md-9 col-lg-10 col-sm-12">
          <h2 className="minipage-title">{category.name}</h2>
          <div className="row">
            {
              productList.map((product: Product) => {
                return (
                  <div className="col-sm-6 col-md-4 col-lg-3 placeholder" key={product.id}>
                    <ProductItem product={product} />
                  </div>
                )
              })
            }
          </div>
          <Pagination
            pagination={pagination}
            productsPerPage={INITIAL_PRODUCT_DISPLAY_COUNT}
            categoryId={category.id}
            pageNum={pageNum}
            productDisplayCountOptions={PRODUCT_DISPLAY_COUNT_OPTIONS}
          />
        </div>
      </div>
    </div>
  )
}