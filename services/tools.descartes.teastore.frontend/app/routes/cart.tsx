import { MetaFunction, LoaderFunction, LoaderFunctionArgs, json, redirect } from "@remix-run/node";
import { useLoaderData } from "@remix-run/react";
import CategoryList from "~/components/categoryList";
import Recommendation from "~/components/recommendation";
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";
import { createPOSTFetcher, createGETFetcher } from "~/.server/request";
import { getSessionBlob } from "~/.server/cookie";
import { OrderItemType, SessionBlobType, Product } from "~/types";
import SessionBlob from "~/model/SessionBlob";

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Cart" },
  ];
};

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

export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  try {
    const orderItems: OrderItemType[] = [];
    const sessionBlob: SessionBlobType = await getSessionBlob(request) || new SessionBlob();
    for (const orderItem of sessionBlob.orderItems || []) {
      orderItems.push(orderItem)
    }
    const uid: number = sessionBlob ? (sessionBlob.uid || 0) : 0;
    const productsRes = await Promise.all(orderItems.map((orderItem) => getProduct(orderItem.productId)))
    const products: Product[] = await Promise.all(productsRes.map((res) => res.json()))
    const productsMap = products.reduce((acc: { [x: string]: Product; }, product: Product) => {
      acc[product.id.toString()] = product;
      return acc;
    }, {})
    const recommendationsRes = await getRecommendations(orderItems, uid);
    const recommendations: number[] = await recommendationsRes.json();
    const recommendedRes = await Promise.all(recommendations.map((productId) => getProduct(productId)))
    let recommendedProducts: Product[] = await Promise.all(recommendedRes.map((res) => res.json()))
    if (recommendedProducts.length > 3) {
      recommendedProducts = recommendedProducts.slice(0, 3)
    }
    let [recommendedImageRes] = await Promise.all([getRecommendedImages(recommendations.map(productId => productId.toString()))])
    const recommendedImages = await recommendedImageRes.json();
    return json({
      orderItems,
      productsMap,
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

export default function CartPage() {
  const { categoryList } = useContext(GlobalStateContext)
  const { productsMap, orderItems, recommendedProducts } = useLoaderData<typeof loader>()
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-sm-9 col-md-6  col-lg-8">
          <h2 className="minipage-title">Shopping Cart</h2>
          <form action="cartAction" method="POST">
            <table className="table table-bordered">
              <thead>
                <tr>
                  <th><b>Item ID</b></th>
                  <th><b>Product Name</b></th>
                  <th><b>Description</b></th>

                  <th><b>Quantity</b></th>
                  <th><b>List Price</b></th>
                  <th><b>Total Cost</b></th>
                  <th>Remove</th>
                </tr>
              </thead>
              <tbody>
                {
                  orderItems.length > 0 ?
                    orderItems.map((orderItem: OrderItemType) => {
                      return (
                        <tr key={orderItem.productId}>
                          <td>{orderItem.productId}<input type='hidden' name="productid" defaultValue={orderItem.productId} /></td>
                          <td>{productsMap[orderItem.productId].name}</td>
                          <td>{productsMap[orderItem.productId].description}</td>
                          <td><input required min="1" name={`orderitem_${orderItem.productId}`} type="number" className="quantity" defaultValue={orderItem.quantity} /></td>
                          <td>${(orderItem.unitPriceInCents / 100.0).toFixed(2)}</td>
                          <td>${(orderItem.unitPriceInCents * orderItem.quantity / 100.0).toFixed(2)}</td>
                          <td>
                            <button type="submit" className="submit-with-icon" name={`removeProduct_${orderItem.productId}`}>
                              <span className="glyphicon glyphicon-trash" aria-hidden="true"></span>
                            </button>
                          </td>
                        </tr>
                      )
                    }) : (
                      <tr>
                        <td colSpan={7}><b>Your cart is empty.</b></td>
                      </tr>
                    )
                }
                <tr>
                  <td colSpan={7}>Total: ${orderItems.reduce((acc: number, item: OrderItemType) => {
                    return acc + item.unitPriceInCents * item.quantity / 100.0
                  }, 0).toFixed(2)} <input
                    name="updateCartQuantities" className="btn" defaultValue="Update Cart" type="submit"></input></td>
                </tr>
              </tbody>
            </table>
            {
              orderItems.length > 0 ?
                <input name="proceedtoCheckout" className="btn" defaultValue="Proceed to Checkout" type="submit" /> : null
            }
          </form>
        </div>
        <Recommendation products={recommendedProducts} />
      </div>
    </div>
  )
}