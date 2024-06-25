import { Product } from '../types';
export default function ProductItem({ product }: { product: Product }) {
  return (
    <div className="thumbnail">
      <form method="POST">
        <table>
          <tr>
            <td className="productthumb">
              <input type='hidden' name="productid" value={product.id} />
              <a href={`/product?id=${product.id}`}>
                <img src={product.image} alt={product.name} />
              </a>
            </td>
            <td className="divider"></td>
            <td className="description">
              <b>{product.name}</b>
              <br />
              <span> Price: ${product.listPriceInCents / 100}</span>
              <br />
              <span> {product.description} </span>
            </td>
          </tr>
        </table>
        <input name="addToCart" className="btn" value="Add to Cart" type="submit" />
      </form>
    </div>
  );
}