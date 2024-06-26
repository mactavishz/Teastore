import { createCookie } from "@remix-run/node"
import appConfig from "~/appConfig"

export const paginationPageSizeCookie = createCookie(appConfig.PAGESIZECOOKIE,{
  maxAge: 604_800, // one week
})

export const sessionBlobCookie = createCookie(appConfig.SESSIONBLOBCOOKIE, {
  maxAge: 604_800, // one week
})

export const errorMessageCookie = createCookie(appConfig.ERRORMESSAGECOOKIE, {
  maxAge: 60, // one minute
})

export const messageCookie = createCookie(appConfig.MESSAGECOOKIE, {
  maxAge: 60, // one minute
})