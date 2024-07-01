import { getURL } from "~/utils/url";

interface HeaderProps {
  storeIcon: string;
  login: boolean;
  message: string | null;
  errorMessage: string | null;
  setMessage: React.Dispatch<React.SetStateAction<string | null>>;
  setErrorMessage: React.Dispatch<React.SetStateAction<string | null>>;
}

export default function Header({ storeIcon, login, message, errorMessage, setMessage, setErrorMessage }: HeaderProps) {
  return (
    <nav id="headnav" className="navbar navbar-default container">
      <div className="container-fluid">
        <div className="navbar-header">
          <button id="navbarbutton" type="button" className="navbar-toggle collapsed"
            data-toggle="collapse" data-target="#navbar" aria-controls="navbar">
            <span className="sr-only">Toggle navigation</span> <span
              className="icon-bar"></span> <span className="icon-bar"></span> <span
                className="icon-bar"></span>
          </button>
          <a className="navbar-brand" href={getURL("/")}>
            <img src={storeIcon} width="30" height="30" className="d-inline-block align-top" alt="" />
            &nbsp; TeaStore
          </a>
        </div>
        <div id="navbar" className="navbar-collapse collapse">
          <ul className="nav navbar-nav navbar-right headnavbarlist">
            {
              login ?
                (
                  <>
                    <li>
                      <form action="loginAction" method="POST">
                        <input type="text" hidden defaultValue="true" name="logout" />
                        <button type="submit" className="logout">Logout</button>
                      </form>
                    </li>
                    <li>
                      <a href={getURL("/profile")}><span className="glyphicon glyphicon glyphicon-user" aria-hidden="true"></span></a>
                    </li>
                  </>
                ) :
                (
                  <li>
                    <a href={getURL("/login")}>Sign in</a>
                  </li>
                )
            }
            <li><a href={getURL("/cart")}><span
              className="glyphicon glyphicon-shopping-cart" aria-hidden="true"></span></a></li>
          </ul>
        </div>
      </div>
      {
        message ?
          <div className="alert alert-success alert-dismissable" role="alert">
            <button type="button" className="close" aria-label="close" onClick={(e) => setMessage(null)}><span aria-hidden="true">&times;</span></button>
            <strong>Success!</strong> {message}
          </div>
          : null
      }
      {
        errorMessage ?
          <div className="alert alert-warning alert-dismissable" role="alert">
            <button type="button" className="close" aria-label="close" onClick={() => setErrorMessage(null)}><span aria-hidden="true">&times;</span></button>
            <strong>Warning!</strong> {errorMessage}
          </div>
          : null
      }
    </nav>
  );
}
