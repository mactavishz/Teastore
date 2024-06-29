import { useContext } from "react"
import { GlobalStateContext } from "~/context/GlobalStateContext"

export function getURL(path: string): string {
  const { baseURL } = useContext(GlobalStateContext)
  if (path.startsWith("/")) {
    return baseURL + path.slice(1)
  } else {
    return baseURL + path
  }
}