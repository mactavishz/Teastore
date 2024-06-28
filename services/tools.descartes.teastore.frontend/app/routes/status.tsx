// export default function StatusPage() {
//   return (
//     <div>
//       Status
//     </div>
//   )
// }



export default function StatusPage() {
  return (
    <div className="container" id="main">
      <div className="row">
        <div className="col-sm-12 col-lg-8 col-lg-offset-2">
          <h2 className="minipage-title">TeaStore Service Status</h2>
          <br/>
          <p><b>This page does not auto refresh!</b> Refresh manually or start an auto refresh for checking the current status (e.g. to see if database generation has finished).</p>
          <p>
            <b>Note:</b> Database and image generation may take a while.
            Leave the TeaStore in a stable and unused state while the database is generating.
            You may use the TeaStore once the database has finished generating.
            Please wait for the image provider to finish as well before running any performance tests.
          </p>
          <br/>
          {/* form and table here: to do */}
        </div>
      </div>
    </div>
  );
}