import { useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import { useCart } from "../context/CartContext";
import { useEffect, useState } from "react";

export default function Card({ vinyl }) {
  const { addToCart, cart, removeFromCart } = useCart();

  const navigate = useNavigate();

  const coverUrl = `https://coverartarchive.org/release/${vinyl.id}/front-250`;

  const inCart = cart
    ? cart.items.some((element) => element.listingId === vinyl.id)
    : false;

  let date;
  let label;
  if (vinyl.date) {
    date = vinyl.date.substring(0, 4);
  } else {
    date = "No date";
  }
  if (vinyl.labelName) {
    if (vinyl.labelName.includes("no label")) {
      label = "";
    } else {
      if (vinyl.labelName.includes("Records")) {
        label = vinyl.labelName;
      } else {
        label = vinyl.labelName + " Records";
      }
    }
  }

  const navigateItemWithId = () => {
    navigate(`/listing/${vinyl.id}`);
  };
  const navigateMessagingWithItemId = () => {
    navigate(`/messaging/${vinyl.id}`);
  };

  return (
    <>
      <div className="bg-neutral-primary-soft  hover:-translate-y-1 duration-200 ease-in-out p-4 border border-default rounded-2xl shadow-xs flex flex-col justify-center">
        <button
          onClick={() => {
            navigateItemWithId();
          }}
        >
          <img
            className="rounded-md h-60 w-60 object-fill"
            src={
              vinyl.imagePaths[0]
                ? `http://localhost:8080/${vinyl.imagePaths[0]}`
                : "/placeholder.png"
            }
            alt=""
          />

          <a href="#">
            <h5 className="mt-3 mb-2 text-2xl font-semibold tracking-tight text-heading w-60">
              {vinyl.title}
            </h5>
          </a>
          <p className="mb-3 text-body">{vinyl.artistName || "No info"}</p>

          <p className="text-body mb-2">{date}</p>

          <p className="text-body">{label || "No Label info"}</p>

          <p className="text-body text-xl mt-5 text-green-500">
            {vinyl.discountedPrice
              ? vinyl.discountedPrice.toLocaleString("tr-TR")
              : vinyl.price.toLocaleString("tr-TR")}{" "}
            ₺
          </p>
        </button>
        <div className="flex flex-row gap-3 justify-center">
          <a
            onClick={() => {
              navigateMessagingWithItemId();
            }}
            className="text-body mt-5 rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-9"
          >
            Trade
          </a>
          <a
            onClick={() =>
              inCart ? removeFromCart(vinyl.id) : addToCart(vinyl.id)
            }
            className={`mt-5 rounded-xl  border   focus:ring-4  shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-4 cursor-pointer min-w-30  hover:text-red-700
              ${inCart ? "bg-green-700 text-white hover:border-red-700" : ""}`}
          >
            {inCart ? "In Cart" : "Add to Cart"}
          </a>
        </div>
      </div>
    </>
  );
}
