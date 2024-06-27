import {
  LoaderFunctionArgs,
  LoaderFunction,
  json,
  redirect,
  ActionFunctionArgs,
  ActionFunction,
  MetaFunction
} from "@remix-run/node";
import { useLoaderData } from "@remix-run/react"
import { createGETFetcher, createPOSTFetcher } from "~/.server/request";
import CategoryList from "~/components/categoryList";
import Recommendation from "~/components/recommendation";
import { useContext } from "react";
import { Product, SessionBlobType, OrderItemType } from "~/types";
import { OrderItem } from "~/model/OrderItem";
import { getSessionBlob } from "~/.server/cookie";
import { GlobalStateContext } from "~/context/GlobalStateContext";

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Product" },
  ];
};


async function getRecommendedImages(productIds: string[]): Promise<Response> {
  let imageMap = {};
  for (const productId of productIds) {
    Object.assign(imageMap, { [productId]: "125x125" })
  }
  const response = await createPOSTFetcher("image", "image/getProductImages", imageMap);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getProductImage(productId: string): Promise<Response> {
  let imageMap = {
    [productId]: "300x300"
  };
  const response = await createPOSTFetcher("image", "image/getProductImages", imageMap);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getProduct(productId: string | number): Promise<Response> {
  const response = await createGETFetcher("persistence", `products/${productId}`);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getRecommendations(orderItems: OrderItemType[], uid: number): Promise<Response> {
  const response = await createPOSTFetcher("recommender", `recommend?uid=${uid}`, orderItems);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}



export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  const url = new URL(request.url);
  let productId = url.searchParams.get("id");

  if (!productId) {
    return redirect(`/`);
  }

  try {
    const [productRes] = await Promise.all([getProduct(productId)]);
    const productData: Product = await productRes.json();
    const orderItems: OrderItemType[] = []
    let productOrderItem = new OrderItem();
    productOrderItem.setProductId(productData.id);
    productOrderItem.setQuantity(1);
    orderItems.push(productOrderItem);
    const sessionBlob: SessionBlobType = await getSessionBlob(request);
    if (sessionBlob) {
      for (const orderItem of sessionBlob.orderItems) {
        orderItems.push(orderItem)
      }
    }
    const uid: number = sessionBlob ? (sessionBlob.uid || 0) : 0;
    const recommendationsRes = await getRecommendations(orderItems, uid);
    const recommendations: number[] = await recommendationsRes.json();
    const recommendedRes = await Promise.all(recommendations.map((productId) => getProduct(productId)))
    let recommendedProducts: Product[] = await Promise.all(recommendedRes.map((res) => res.json()))
    if (recommendedProducts.length > 3) {
      recommendedProducts = recommendedProducts.slice(0, 3)
    }
    let [productImageRes, recommendedImageRes] = await Promise.all([getProductImage(productId), getRecommendedImages(recommendations.map(productId => productId.toString()))])
    const productImage = await productImageRes.json();
    const recommendedImages = await recommendedImageRes.json();
    return json({
      product: {
        ...productData,
        image: productImage[productId]
      },
      recommendedProducts: recommendedProducts.map((product) => {
        return {
          ...product,
          image: recommendedImages[product.id]
        }
      })
    })
  } catch (err) {
    console.error(err)
    throw new Response("An error occurred", { status: 500 });
  }
}

export default function ProductPage() {
  const { categoryList } = useContext(GlobalStateContext)
  const { product, recommendedProducts } = useLoaderData<typeof loader>();
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-sm-6 col-md-6 col-lg-8">
          <form action="cartAction" method="POST">
            <div className="row">
              <input type='hidden' name="productid" defaultValue={product.id} />
              <div className="col-sm-12 showcase"><div>
                <h2 className="minipage-title">{product.name}</h2>
                <img className="productpicture" src={product.image} alt={product.name} />
                <blockquote>{product.description}</blockquote>
                <span>Price: ${(product.listPriceInCents / 100.0).toFixed(2)}</span><br />
                <input name="addToCart" className="btn" defaultValue="Add to Cart" type="submit" />
              </div>
              </div>
            </div>
          </form>
        </div>
        <Recommendation products={recommendedProducts} />
      </div>
    </div>
  )
}