import { json, MetaFunction, LoaderFunction, LoaderFunctionArgs } from "@remix-run/node";
import { useLoaderData } from "@remix-run/react";
import CategoryList from "~/components/categoryList";
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";
import ErrorMessage from "~/components/error";

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Login" },
  ];
};

export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  return json({
    referer: request.headers.get("Referer"),
  });
}

export default function LoginPage() {
  const { categoryList } = useContext(GlobalStateContext);
  const { referer } = useLoaderData<typeof loader>();
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-sm-6 col-lg-8">
          <h2 className="minipage-title">Login</h2>
          <form action="loginAction" method="POST">
            <div className="row">
              <h4 className="advertismenttitle">Please enter your username and password.</h4>
              <input type="hidden" name="referer" defaultValue={referer} />
              <div className="col-sm-8 col-md-8 col-lg-4">
                <div className="form-group row">
                  <label htmlFor="username" className="col-sm-4 col-form-label col-form-label-lg">Username</label>
                  <div className="col-sm-8">
                    <input type="text" className="form-control form-control-lg"
                      name="username" id="username" defaultValue="user2" placeholder="user"
                      required />
                  </div>
                </div>
                <div className="form-group row">
                  <label htmlFor="password"
                    className="col-sm-4 col-form-label col-form-label-lg">Password</label>
                  <div className="col-sm-8">
                    <input type="password" className="form-control form-control-lg"
                      name="password" id="password" defaultValue="password"
                      placeholder="password" required />
                  </div>
                </div>
                <input className="btn" name="signin" value="Sign in" type="submit" />
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export function ErrorBoundary() {
  return <ErrorMessage />;
}