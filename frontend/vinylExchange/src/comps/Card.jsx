import { useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import { useCart } from "../context/CartContext";

export default function Card({ vinyl }) {
  const { addToCart } = useCart();
  const navigate = useNavigate();
  const coverUrl = `https://coverartarchive.org/release/${vinyl.id}/front-250`;
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
  // console.log(vinyl);

  const navigateItemWithId = () => {
    // console.log(vinyl.id);
    navigate(`/listing/${vinyl.id}`);
  };

  return (
    <button
      onClick={() => {
        navigateItemWithId();
      }}
    >
      <div className="bg-neutral-primary-soft  max-w-sm p-6 border border-default rounded-2xl shadow-xs flex flex-col justify-center">
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
          <h5 className="mt-3 mb-2 text-2xl font-semibold tracking-tight text-heading">
            {vinyl.title}
          </h5>
        </a>
        <p className="mb-3 text-body">{vinyl.artistName || "No info"}</p>

        <p className="text-body mb-2">{date}</p>

        <p className="text-body">{label || "No Label info"}</p>

        <p className="text-body text-xl mt-5 text-green-500">
          {vinyl.discountedPrice ? vinyl.discountedPrice : vinyl.price} ₺
        </p>

        <div className="flex flex-row gap-3 justify-center">
          <a
            href="#"
            className="text-body mt-5 rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-9"
          >
            Trade
          </a>
          <a
            onClick={() => addToCart(vinyl.id)}
            className=" mt-5 rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-4"
          >
            Add to Cart
          </a>
        </div>
      </div>
    </button>
  );
}
