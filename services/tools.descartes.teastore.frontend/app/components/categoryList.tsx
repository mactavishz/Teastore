interface Category {
  id: number;
  name: string;
  description: string;
}

export default function CategoryList({ list = [] }) {
  return (
    <div className="col-sm-3 col-md-3 col-lg-2 sidebar">
      <h3>Categories</h3>
      <ul className="nav-sidebar">
        {
          list.map((category: Category) => {
            return (
              <li className="category-item" role="presentation">
                <a
                  href={`/category?category=${category.id}&page=1`}
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