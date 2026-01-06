import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";

export default function ListView({ item }) {
  const { addToCart, cart, removeFromCart } = useCart();

  const [image, setImage] = useState("");

  const navigate = useNavigate();

  const navigateItemWithId = () => {
    navigate(`/listing/${item.id}`);
  };

  const inCart = cart
    ? cart.items.some((element) => element.listingId === item.id)
    : false;

  return (
    <>
      <div className="bg-neutral-primary-soft ml-1 pb-5 gap-3 grid grid-cols-6 border-b max-w-[1160px] mb-5 items-center">
        <button
          onClick={() => {
            navigateItemWithId();
          }}
        >
          <img
            src={`http://localhost:8080/${item.imagePaths[0]}`}
            onError={(e) => {
              e.target.src = "/placeholder.png";
            }}
            alt="listing main image"
            className="bg-black"
          />
        </button>
        <button
          onClick={() => {
            navigateItemWithId();
          }}
        >
          <p
            scope="row"
            className="px-6 py-4 font-medium text-heading flex flex-col overflow-auto "
          >
            {item.title}
          </p>
        </button>

        <p className="px-6 py-4">{item.date} </p>
        <p className={"px-6 py-4"}>{item.format} </p>
        {/* <p className="px-6 py-4">{item.trackCount}</p> */}
        <div className="">
          <p
            className={`px-6 ${
              item.discount > 0
                ? "text-base font-bold text-gray-900 dark:text-white line-through"
                : "text-base font-bold text-gray-900 dark:text-white"
            }`}
          >
            {item.price + " ₺"}
          </p>
          <p
            className={`px-6 ${
              item.discount > 0
                ? "text-base font-bold text-green-900 dark:text-green-400"
                : "text-base font-bold text-green-900 dark:text-green-400"
            }`}
          >
            {item.discount > 0 ? item.discountedPrice + " ₺" : null}
          </p>
        </div>
        {/* <p className="px-6 py-4">{item.price}</p> */}

        <div className="px-6 py-4 text-right">
          <div className="flex flex-col justify-center items-center -mt-5">
            <a
              onClick={() =>
                inCart ? removeFromCart(item.id) : addToCart(item.id)
              }
              className={`mt-5 rounded-xl  border   focus:ring-4  shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-4.5 cursor-pointer min-w-30  hover:text-red-700
              ${inCart ? "bg-green-700 text-white hover:border-red-700" : ""}`}
            >
              {inCart ? "In Cart" : "Add to Cart"}
            </a>
            <a
              href="/messaging"
              className="text-body mt-2 rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-10.5"
            >
              Trade
            </a>
          </div>
        </div>
      </div>
    </>
  );
}
