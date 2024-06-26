import type { MetaFunction } from "@remix-run/node";
import CategoryList from "~/components/categoryList";
import { useContext } from "react";
import { GlobalStateContext } from "~/context/GlobalStateContext";

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Login" },
  ];
};

export default function LoginPage() {
  const { categoryList } = useContext(GlobalStateContext);
  return (
    <div className="container" id="main">
      <div className="row">
        <CategoryList list={categoryList} />
        <div className="col-sm-6 col-lg-8">
          <h2 className="minipage-title">Login</h2>
          <form action="loginAction" method="POST">
            <div className="row">
              <h4 className="advertismenttitle">Please enter your username and password.</h4>
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