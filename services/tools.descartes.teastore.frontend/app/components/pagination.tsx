interface PaginationProps {
  pagination: string[];
  categoryId: number;
  pageNum: number;
  productDisplayCountOptions: number[];
  productsPerPage: number;
}
export default function Pagination({ pagination, categoryId, pageNum, productDisplayCountOptions, productsPerPage }: PaginationProps) {
  return (
    <div className="row">
      <div className="col-sm-6">
        <ul className="pagination">
          {pagination.map((item: string) => {
            let displayPageNum;
            if (item == "previous") {
              displayPageNum = pageNum - 1;
            } else if (item == "next") {
              displayPageNum = pageNum + 1;
            } else {
              displayPageNum = item;
            }
            return (
              <li className={item == pageNum.toString() ? "active" : ""}>
                <a href={`/category?category=${categoryId}&page=${displayPageNum}`}>
                  {item}
                </a>
              </li>
            )
          })}
        </ul>
      </div>
      <div className="col-sm-6">
        <form id="formpages">
          <select name="number" onChange={() => null} defaultValue={productsPerPage}>
            {
              productDisplayCountOptions.map((number: number) => {
                return (
                  <option value={number}>{number}</option>
                )
              })
            }
          </select> <span> products per page</span>
        </form>
      </div>
    </div>
  )
}