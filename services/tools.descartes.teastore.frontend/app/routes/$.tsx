import { LoaderFunction, redirect } from "@remix-run/node";

export const loader: LoaderFunction = async ({ request }) => {
  // You can add logic here to check if the route should be a 404
  // For example, you might want to check against a list of valid routes

  // If it's a 404, redirect to your error page
  throw redirect('/error?status=404&message=Page not found');
};

export default function CatchAllRoute() {
  // This component won't be rendered because we're always redirecting
  // But we need to export a default function for the route to be valid
  return null;
}