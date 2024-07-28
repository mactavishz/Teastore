<%@ taglib prefix="c" uri="jakarta.tags.core"%><%@ taglib uri="jakarta.tags.functions" prefix="fn"%><%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<div class="col-sm-3 col-md-3 col-lg-2 sidebar">
	<h3>Categories</h3>
	<ul class="nav-sidebar">
		<c:forEach items="${CategoryList}" var="category">
			<li class="category-item" role="presentation"><a
				href="<c:url value="/category?category=${category.id}&page=1" />"
				class="menulink" id="link_${category.name}">${category.name}<br>
					<span>${category.description}</span></a></li>
		</c:forEach>
	</ul>
</div>