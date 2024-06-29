import { useSubmit } from "@remix-run/react";
import { useRef } from "react";
import { getURL } from "~/utils/url";

interface PaginationProps {
  pagination: string[];
  categoryId: number;
  pageNum: number;
  productDisplayCountOptions: number[];
  pageSize: number;
}

export default function Pagination({ pagination, categoryId, pageNum, productDisplayCountOptions, pageSize }: PaginationProps) {
  const submit = useSubmit();
  const formRef = useRef<HTMLFormElement>(null);
  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    submit(event.currentTarget.form);
  };
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
              <li className={item == pageNum.toString() ? "active" : ""} key={`page-button-${item}`}>
                <a href={getURL(`/category?category=${categoryId}&page=${displayPageNum}`)}>
                  {item}
                </a>
              </li>
            )
          })}
        </ul>
      </div>
      <div className="col-sm-6">
        <form id="formpages" ref={formRef} action="" method="POST">
          <select name="newPageSize" onChange={handleChange} defaultValue={pageSize}>
            {
              productDisplayCountOptions.map((number: number) => {
                return (
                  <option key={`paganation-option-${number}`} value={number}>{number}</option>
                )
              })
            }
          </select> <span> products per page</span>
          <input name="categoryId" type="text" defaultValue={categoryId} hidden />
        </form>
      </div>
    </div>
  )
}