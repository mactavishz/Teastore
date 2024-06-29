import type { Category } from "~/types";
import { getURL } from "~/utils/url";

export default function CategoryList({ list = [] }: { list: Category[] }) {
  return (
    <div className="col-sm-3 col-md-3 col-lg-2 sidebar">
      <h3>Categories</h3>
      <ul className="nav-sidebar">
        {
          list.map((category: Category) => {
            return (
              <li className="category-item" role="presentation" key={category.id}>
                <a
                  href={getURL(`/category?category=${category.id}&page=1`)}
                  className="menulink"
                  id={`link_${category.name}`}
                >
                  {category.name}<br />
                  <span>{category.description}</span>
                </a>
              </li>
            )
          })
        }
      </ul>
    </div>
  )
}