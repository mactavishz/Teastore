<div class="col-sm-3 col-md-3 col-lg-2">
	<div id="advertisementContainer" class="row">
	</div>
</div>

<script>
$(document).ready(function() {
	let url;
	let searchParams = new URLSearchParams(window.location.search)
	if (window.location.href.includes('product') && searchParams.has('id')) {
		url = '${pageContext.request.contextPath}/rest/recommendation/ads?pid=' + searchParams.get('id');
	} else {
		url = '${pageContext.request.contextPath}/rest/recommendation/ads';
	}
    $.ajax({
        url,
        method: 'GET',
        success: function(data) {
            if (data && data.length > 0) {
				html = '';
                html += '<h4 class="advertismenttitle">Are you interested in?</h4>';
                data.forEach(function(product) {
                    html += '<div class="col-sm-12 placeholder">';
                    html += generateProductItem(product);
                    html += '</div>';
                });
                $('#advertisementContainer').html(html);
            }
        },
        error: function() {
            console.error('Failed to load advertisement');
        }
    });
});

function generateProductItem(product) {
    return '<div class="thumbnail">' +
        '<form action="cartAction" method="POST">' +
            '<table><tr><td class="productthumb">' +
                '<input type="hidden" name="productid" value="' + product.id + '">' +
                '<a href="${pageContext.request.contextPath}/product?id=' + product.id + '">' +
                    '<img src="' + "${productPreviewImageBaseURL}" + product.id + ".png" + '" alt="' + product.name + '">' +
                '</a>' +
            '</td>' +
            '<td class="divider"></td>' +
            '<td class="description">' +
                '<b>' + product.name + '</b><br>' +
                '<span>Price: $' + (product.listPriceInCents/100).toFixed(2) + '</span><br>' +
                '<span>' + product.description + '</span>' +
            '</td></tr></table>' +
            '<input name="addToCart" class="btn" value="Add to Cart" type="submit">' +
        '</form>' +
    '</div>';
}
</script>