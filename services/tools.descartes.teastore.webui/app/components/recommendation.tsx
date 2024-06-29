import { Product } from '../types';
import ProductItem from './productItem';

interface RecommendationProps {
  products: Product[];
}

export default function Recommendation({ products = [] }: RecommendationProps) {
  return (
    <div className="col-sm-3 col-md-3 col-lg-2">
      <div className="row">
        <h4 className="advertismenttitle">Are you interested in?</h4>
        {
          products.map((product: Product) => {
            return (
              <div className="col-sm-12 placeholder" key={product.id}>
                <ProductItem product={product} />
              </div>
            )
          })
        }
      </div>
    </div>
  )
}