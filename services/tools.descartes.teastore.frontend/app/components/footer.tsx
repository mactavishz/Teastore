export default function Footer() {
  return (
    <nav id="footnav"
      className="navbar navbar-default navbar-fixed-bottom container"
      style={{ height: "75px" }}
    >
      <div className="container-fluid">

        <div className="navbar-header"></div>
        <div id="navbarbottom" className="navbar-collapse collapse in">
          <ul className="nav navbar-nav navbar-left">
            <li><a href="http://www.descartes.tools" target="_blank">www.descartes.tools</a></li>
          </ul>
          <ul className="nav navbar-nav navbar-right">
            {/* <li><a href="/database">Database</a></li> */}
            <li><a href="/status">Status</a></li>
            <li><a href="/about">About us</a></li>
            <li><a
              href="https://github.com/DescartesResearch/TeaStore/wiki"><span
                className="glyphicon glyphicon-question-sign" aria-hidden="true"></span></a></li>
          </ul>
        </div >
      </div >
    </nav >
  )
}