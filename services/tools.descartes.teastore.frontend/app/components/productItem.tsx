import { Product } from '../types';
import { getURL } from "~/utils/url";

export default function ProductItem({ product }: { product: Product }) {
  return (
    <div className="thumbnail">
      <form action="cartAction" method="POST">
        <table>
          <tbody>
            <tr>
              <td className="productthumb">
                <input type='hidden' name="productid" value={product.id} />
                <a href={getURL(`/product?id=${product.id}`)}>
                  <img src={product.image} alt={product.name} />
                </a>
              </td>
              <td className="divider"></td>
              <td className="description">
                <b>{product.name}</b>
                <br />
                <span> Price: ${product.listPriceInCents / 100.0}</span>
                <br />
                <span> {product.description} </span>
              </td>
            </tr>
          </tbody>
        </table>
        <input name="addToCart" className="btn" defaultValue="Add to Cart" type="submit" />
      </form>
    </div>
  );
}