import { MetaFunction, LoaderFunction, LoaderFunctionArgs, json, redirect } from "@remix-run/node";
import { useLoaderData } from "@remix-run/react";
import CategoryList from "~/components/categoryList";
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";
import { useLoginStatus  } from "~/.server/loginUtil";
import { errorMessageCookie, getSessionBlob } from "~/.server/cookie";
import { createGETFetcher } from "~/.server/request";
import { SessionBlobType, OrderType } from "~/types";
import appConfig from "~/appConfig";
import { parseISO, format } from 'date-fns';

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Profile" },
  ];
};

function formatDate(date: string = "") {
  return format(parseISO(date), 'yyyy-MM-dd HH:mm:ss');
}

async function getUser(uid: number | null): Promise<Response> {
  const response = await createGETFetcher("persistence", `users/${uid}`);
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

async function getOrders(uid: number | null): Promise<Response> {
  const response = await createGETFetcher("persistence", `orders/user/${uid}`, {
    start: -1,
    max: -1
  });
  if (!response.ok) {
    throw new Response("Failed to fetch data", { status: response.status });
  }
  return response;
}

export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  try {
    const loginStatus = await useLoginStatus({ request });
    if (!loginStatus) {
      return redirect("/", {
        headers: [
          ["Set-Cookie", await errorMessageCookie.serialize(appConfig.UNAUTHORIZED)],
        ]
      });
    }
    const sessionBlob: SessionBlobType = await getSessionBlob(request);
    const [userRes, ordersRes] = await Promise.all([getUser(sessionBlob.uid), getOrders(sessionBlob.uid)]);
    const userData = await userRes.json();
    const ordersData = await ordersRes.json();
    return json({ user: userData, orders: ordersData });
  } catch (err) {
    console.log(err)
    throw new Response("An error occurred", { status: 500 });
  }
}

export default function ProfilePage() {
  const { categoryList } = useContext(GlobalStateContext)
  const { user, orders } = useLoaderData<typeof loader>();
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-sm-9 col-md-9 col-lg-10">
          <div className="row">
            <div className="col-sm-6">
              <h4>User Information</h4>
              <table className="table table-bordered">
                <tbody>
                  <tr>
                    <th>Username</th>
                    <td>{user.userName}</td>
                  </tr>
                  <tr>
                    <th>Real Name</th>
                    <td>{user.realName}</td>
                  </tr>
                  <tr>
                    <th>Email</th>
                    <td>{user.email}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <h4>Orders</h4>
          <table className="table table-bordered">
            <thead>
              <tr>
                <th>ID</th>
                <th>Time</th>
                <th>Price</th>
                <th>Address Name</th>
                <th>Address</th>
              </tr>
            </thead>
            <tbody>
              {
                orders.map((order: OrderType) => {
                  return (
                    <tr key={order.id}>
                      <td>{order.id}</td>
                      <td>{formatDate(order.time || "")}</td>
                      <td>${order.totalPriceInCents / 100.0}</td>
                      <td>{order.addressName}</td>
                      <td>{order.address1}, {order.address2}</td>
                    </tr>
                  )
                })
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}