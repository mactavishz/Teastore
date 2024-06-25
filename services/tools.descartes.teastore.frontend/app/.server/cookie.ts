import { createCookie } from "@remix-run/node"

export const paginationPageSizeCookie = createCookie("TeaStorePageSize",{
  maxAge: 604_800, // one week
})