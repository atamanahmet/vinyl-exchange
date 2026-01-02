import { createPortal } from "react-dom";

export default function ErrorPage() {
  let errorRoot = document.getElementById("error-root");
  if (!errorRoot) {
    errorRoot = document.createElement("div");
    errorRoot.id = "error-root";
    document.body.appendChild(errorRoot);
  }
  return createPortal(
    <section class="bg-white dark:bg-gray-900 w-screen h-screen text-center -ml-20 flex justify-center items-center">
      <div class=" max-w-7xl lg:py-16 lg:px-6">
        <div class="text-center">
          <h1 class="mb-4 text-7xl tracking-tight font-extrabold lg:text-9xl text-primary-600 dark:text-primary-500">
            500
          </h1>
          <p class="mb-4 text-3xl tracking-tight font-bold text-gray-900 md:text-4xl dark:text-white">
            Internal Server Error.
          </p>
          <p class="mb-10 text-lg font-light text-gray-500 dark:text-gray-400">
            We are already working to solve the problem.{" "}
          </p>
        </div>
        <a href="/" className="border p-2 rounded-md px-4  text-indigo-500">
          Home
        </a>
      </div>
    </section>,
    errorRoot
  );
}
