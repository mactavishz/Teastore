export default function Header({ storeIcon, login, message, errormessage }: { storeIcon: string, login: boolean, message: string, errormessage: string }) {
  return (
    <nav id="headnav" className="navbar navbar-default container" style={{ height: "75px"}}>
      <div className="container-fluid">
        <div className="navbar-header">
          <button id="navbarbutton" type="button" className="navbar-toggle collapsed"
            data-toggle="collapse" data-target="#navbar" aria-controls="navbar">
            <span className="sr-only">Toggle navigation</span> <span
              className="icon-bar"></span> <span className="icon-bar"></span> <span
                className="icon-bar"></span>
          </button>
          <a className="navbar-brand" href="/">
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
                        <button type="submit" name="logout" className="logout">Logout</button>
                      </form>
                    </li>
                    <li>
                      <a href="/profile"><span className="glyphicon glyphicon glyphicon-user" aria-hidden="true"></span></a>
                    </li>
                  </>
                ) :
                (
                  <li>
                    <a href="/login">Sign in</a>
                  </li>
                )
            }
            <li><a href="/cart"><span
              className="glyphicon glyphicon-shopping-cart" aria-hidden="true"></span></a></li>
          </ul>
        </div>
      </div>
      {
        message ?
          <div className="alert alert-success alert-dismissable" role="alert">
            <a href="#" className="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Success!</strong> {message}
          </div>
          : null
      }
      {
        errormessage ?
          <div className="alert alert-warning alert-dismissable" role="alert">
            <a href="#" className="close" data-dismiss="alert" aria-label="close">&times;</a>
            <strong>Warning!</strong> {errormessage}
          </div>
          : null
      }
    </nav>
  );
}
