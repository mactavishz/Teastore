import { MetaFunction, LoaderFunction, LoaderFunctionArgs, json, redirect } from "@remix-run/node";
import { useLoaderData } from "@remix-run/react";
import CategoryList from "~/components/categoryList";
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";
import { createPOSTFetcher, createGETFetcher } from "~/.server/request";
import { getSessionBlob, errorMessageCookie } from "~/.server/cookie";
import { OrderItemType, SessionBlobType, Product } from "~/types";
import { useLoginStatus } from "~/.server/loginUtil";
import SessionBlob from "~/model/SessionBlob";
import appConfig from "~/appConfig";
import ErrorMessage from "~/components/error";

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Order" },
  ];
};

export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  try {
    const loginStatus = await useLoginStatus({ request });
    if (!loginStatus) {
      return redirect("/", {
        headers: [
          ["Set-Cookie", await errorMessageCookie.serialize(appConfig.UNAUTHORIZED)],
        ]
      });
    } else {
      const sessionBlob: SessionBlobType = await getSessionBlob(request) || new SessionBlob();
      const orderItems: OrderItemType[] = sessionBlob.orderItems || [];
      if (orderItems.length === 0) {
        return redirect("/", {
          headers: [
            ["Set-Cookie", await errorMessageCookie.serialize("The cart is empty. Please add items to the cart before proceeding.")],
          ]
        });
      }
      return null
    }
  } catch (err) {
    console.error(err)
    throw new Response("An error occurred", { status: 500 });
  }
}

export default function OrderPage() {
  const { categoryList } = useContext(GlobalStateContext)
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-sm-6 col-lg-8">
          <h2 className="minipage-title">Order</h2>
          <form action="cartAction" method="POST">
            <div className="row">
              <div className="col-sm-12 col-md-12 col-lg-4">
                <h4 className="minipage-title">Billing Address</h4>
                <div className="form-group row">
                  <label htmlFor="firstname" className="col-sm-6 col-md-5 col-form-label col-form-label-lg">First Name</label>
                  <div className="col-sm-6 col-md-7">
                    <input type="text" className="form-control form-control-lg" name="firstname" id="firstname" placeholder="Joe" defaultValue="Jon" required />
                  </div>
                </div>
                <div className="form-group row">
                  <label htmlFor="lastname" className="col-sm-6 col-md-5 col-form-label col-form-label-lg">Last Name</label>
                  <div className="col-sm-6 col-md-7">
                    <input type="text" className="form-control form-control-lg" name="lastname" id="lastname" placeholder="Doe" defaultValue="Snow" required />
                  </div>
                </div>
                <div className="form-group row">
                  <label htmlFor="address1" className="col-sm-6 col-md-5 col-form-label col-form-label-lg">Address 1</label>
                  <div className="col-sm-6 col-md-7">
                    <input type="text" className="form-control form-control-lg" name="address1" id="address1" placeholder="901 San Antonio Road" defaultValue="Winterfell" required />
                  </div>
                </div>
                <div className="form-group row">
                  <label htmlFor="adress2" className="col-sm-6 col-md-5 col-form-label col-form-label-lg">Address 2</label>
                  <div className="col-sm-6 col-md-7">
                    <input type="text" className="form-control form-control-lg" name="address2" id="address2" placeholder="MS UCUP02-206" defaultValue="11111 The North, Westeros" required />
                  </div>
                </div>
              </div>
              <div className="col-sm-12 col-md-12 col-lg-4">
                <h4 className="minipage-title">Payment Details</h4>
                <div className="form-group row">
                  <label htmlFor="cardtype" className="col-sm-6 col-md-5 col-form-label col-form-label-lg">Card Type</label>
                  <div className="col-sm-6 col-md-7">
                    <select className="form-control form-control-lg" name="cardtype" id="cardtype">
                      <option value="volvo">Visa</option>
                      <option value="saab">MasterCard</option>
                      <option value="fiat">American Express</option>
                    </select>
                  </div>
                </div>
                <div className="form-group row">
                  <label htmlFor="cardnumber" className="col-sm-6 col-md-5 col-form-label col-form-label-lg">Card Number</label>
                  <div className="col-sm-6 col-md-7">
                    <input type="number" min="0" className="form-control form-control-lg" name="cardnumber" id="cardnumber" placeholder="314159265359" defaultValue="314159265359" required />
                  </div>
                </div>
                <div className="form-group row">
                  <label htmlFor="expirydate" className="col-sm-6 col-md-5 col-form-label col-form-label-lg">Expiry Date (MM/YYYY)</label>
                  <div className="col-sm-6 col-md-7">
                    <input type="text" className="form-control form-control-lg" name="expirydate" id="expirydate" placeholder="12/2025" defaultValue="12/2025" pattern="(0[1-9]|1[012])[\/](19|20)\d\d" required />
                  </div>
                </div>
              </div>
            </div>
            <input className="btn" name="confirm" defaultValue="Confirm" type="submit" />
          </form>
        </div>
      </div>
    </div>
  )
}

export function ErrorBoundary() {
  return <ErrorMessage />;
}