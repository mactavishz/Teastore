<%@include file="head.jsp"%>



<div class="container" id="main">
	<div class="row">
			<div class="col-sm-12 col-lg-8 col-lg-offset-2">
				<h2 class="minipage-title">TeaStore Service Status</h2>
				<br/>
				<p><b>This page does not auto refresh!</b> Refresh manually or start an auto refresh for checking the current status (e.g. to see if database generation has finished).</p>
				<br/>
				<form id="refreshForm">
					<label for="refreshDurationField">Refresh Duration(s)</label>
					<input style="border: 1px solid black;" id="refreshDurationField" type="number" min="0" max="120" class="btn" name="duration">
					<input type="submit" class="btn" value="Start Auto Refresh">
				</form>
                <br/>
				<table class="table">
					<tr>
						<td><b>Service</b></td>
						<td><b>Host(s)</b></td>
						<td><b>Status</b></td>
					</tr>
					<tr>
						<td>WebUI</td>
						<td>
							<c:forEach items="${webuiservers}" var="server" varStatus="loop">
								${server}<br/>
							</c:forEach>
						</td>
						<td class="success">OK</td>
					</tr>
					<tr>
						<td>Auth</td>
						<td>
							<c:forEach items="${authenticationservers}" var="server" varStatus="loop">
								${server}<br/>
							</c:forEach>
						</td>
						<c:choose>
							<c:when test = "${authenticationservers.size() > 0}">
							   <td class="success">OK</td>
							</c:when>
							<c:otherwise>
							   <td class="danger">Offline</td>
							</c:otherwise>
 						</c:choose>
					</tr>
					<tr>
						<td>Persistence</td>
						<td>
							<c:forEach items="${persistenceservers}" var="server" varStatus="loop">
								${server}<br/>
							</c:forEach>
						</td>
						<c:choose>
							<c:when test = "${persistenceservers.size() < 1}">
							   <td class="danger">Offline</td>
							</c:when>
							<c:otherwise>
								<td class="success">OK</td>
							</c:otherwise>
 						</c:choose>
					</tr>
					<tr>
						<td>Recommender</td>	
   						<td>
							<c:forEach items="${recommenderservers}" var="server" varStatus="loop">
								${server}<br/>
							</c:forEach>
						</td>
   						<c:choose>
   							<c:when test = "${recommenderservers.size() < 1}">
							   <td class="danger">Offline</td>
							</c:when>
				         	<c:otherwise>
			            		<td class="success">OK</td>
				        	</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<td>Image</td>
   						<td>
							<c:forEach items="${imageservers}" var="server" varStatus="loop">
								${server}<br/>
							</c:forEach>
						</td>
      					<c:choose>
      						<c:when test = "${imageservers.size() < 1}">
							   <td class="danger">Offline</td>
							</c:when>
				         	<c:otherwise>
			            		<td class="success">OK</td>
				        	</c:otherwise>
						</c:choose>
					</tr>
				</table>
				<input type="button" class="btn errorbtn" value="Back to Shop" onclick="location.href = '<c:url value='/' />';">
			</div>
		</div>
</div>


<!-- Bootstrap core JavaScript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="<c:url value="bootstrap/js/jquery.min.js"/>"></script>
<script src="<c:url value="bootstrap/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/resizingscript.js"/>"></script>
<script src="<c:url value="/autoRefreshScript.js"/>"></script>

</body>
</html>


