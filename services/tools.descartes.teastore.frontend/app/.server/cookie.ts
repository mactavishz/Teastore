import { createCookie } from "@remix-run/node"
import appConfig from "~/appConfig"
import SessionBlob from "~/model/SessionBlob"

export const paginationPageSizeCookie = createCookie(appConfig.PAGESIZECOOKIE,{
  maxAge: 604_800, // one week
})

export const sessionBlobCookie = createCookie(appConfig.SESSIONBLOBCOOKIE, {
  maxAge: 604_800, // one week
})

export const getSessionBlob = async (request: Request): Promise<SessionBlob> => {
  const cookie = request.headers.get("Cookie")
  return await sessionBlobCookie.parse(cookie)
}

export const errorMessageCookie = createCookie(appConfig.ERRORMESSAGECOOKIE, {
  maxAge: 60, // one minute
})

export const messageCookie = createCookie(appConfig.MESSAGECOOKIE, {
  maxAge: 60, // one minute
})