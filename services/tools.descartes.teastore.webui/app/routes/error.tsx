import { json, MetaFunction, LoaderFunction, LoaderFunctionArgs } from "@remix-run/node";
import { useLoaderData } from "@remix-run/react";
import ErrorMessage from "~/components/error";

type LoaderData = {
  status: number;
  message: string;
};

export const meta: MetaFunction = () => {
  return [
    { title: "TeaStore Error" },
  ];
};

export const loader: LoaderFunction = async ({ request }: LoaderFunctionArgs) => {
  const url = new URL(request.url);
  const status = parseInt(url.searchParams.get("status") || "500", 10);
  const message = url.searchParams.get("message") || "An unexpected error occurred";
  return json<LoaderData>({ status, message });
};

export default function ErrorPage() {
  const { status, message } = useLoaderData<LoaderData>();
  const errorMessage = `Error ${status}: ${message}`;
  return  (
    <ErrorMessage errorMessage={errorMessage} />
  )
}