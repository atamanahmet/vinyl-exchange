import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import TradeButton from "./Buttons/TradeButton";
import { useCartStore } from "../stores/cartStore";
import { useAuthStore } from "../stores/authStore";
import { useMessagingStore } from "../stores/messagingStore";

export default function Card({ item }) {
  return (
    <div className="bg-neutral-primary-soft  hover:-translate-y-1 duration-200 ease-in-out p-4 border border-default rounded-2xl shadow-xs flex flex-col justify-center">
      <img
        src={item.imageUrl}
        className="rounded-md h-60 w-60 object-cover"
        alt=""
        onError={(e) => {
          e.currentTarget.onerror = null;
          e.currentTarget.src = "/placeholder.png";
        }}
      />

      <a href="#">
        <h5 className="mt-3 mb-2 text-2xl font-semibold tracking-tight text-heading ">
          {item.title}
        </h5>
      </a>
      <p className="mb-3 text-body">{item.artist}</p>
      <p className="mb-3 text-body">{item.format}</p>
      <p className="text-body mb-2">{item.year}</p>
      <p className="text-body">{item.label}</p>

      {item.price && (
        <p className="text-body text-xl mt-5 text-green-500">
          {(item.discountedPrice ?? item.price).toLocaleString("tr-TR")} ₺
        </p>
      )}

      <button
        className="text-body mt-5 cursor-pointer rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-9"
        onClick={item.primaryAction.onClick}
      >
        {item.primaryAction.label}
      </button>

      {item.secondaryAction && (
        <button
          className="text-body mt-5 cursor-pointer rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-9"
          onClick={item.secondaryAction.onClick}
        >
          {item.secondaryAction.label}
        </button>
      )}
    </div>
  );
}

// export default function Card({ item }) {

//   const user = useAuthStore((state) => state.user);

//   const addToCart = useCartStore((state) => state.addtoCart);
//   const removeFromCart = useCartStore((state) => state.removeFromCart);
//   const decreaseFromCart = useCartStore((state) => state.decreaseFromCart);
//   const cart = useCartStore((state) => state.cart);

//   const startConversation = useMessagingStore(
//     (state) => state.startConversation,
//   );

//   const [date, setDate] = useState();
//   const [label, setLabel] = useState();
//   const [isOwnerUser, setIsOwnerUser] = useState();

//   const navigate = useNavigate();

//   const coverUrl = `https://coverartarchive.org/release/${item.id}/front-250`;

//   const inCart = cart
//     ? cart.items.some((element) => element.listingId == item.id)
//     : false;

//   useEffect(() => {
//     if (item && user && item.ownerUsername === user.username) {
//       setIsOwnerUser(true);
//     } else {
//       setIsOwnerUser(false);
//     }
//   }, [item, user]);

//   useEffect(() => {
//     if (item.date) {
//       setDate(item.date.substring(0, 4));
//     } else {
//       setDate("No date");
//     }

//     if (item.labelName) {
//       if (item.labelName.includes("no label")) {
//         setLabel("");
//       } else {
//         if (item.labelName.includes("Records")) {
//           setLabel(item.labelName);
//         } else {
//           setLabel(item.labelName + " Records");
//         }
//       }
//     }
//   }, []);

//   const navigateItemWithId = () => {
//     navigate(`/listing/${item.id}`);
//   };
//   const navigateEditItemWithId = () => {
//     navigate(`/edit/${item.id}`);
//   };

//   return (
//     <>
//       <div className="bg-neutral-primary-soft  hover:-translate-y-1 duration-200 ease-in-out p-4 border border-default rounded-2xl shadow-xs flex flex-col justify-center">
//         <button
//           onClick={() => {
//             navigateItemWithId();
//           }}
//         >
//           <img
//             className="rounded-md h-60 w-60 object-cover"
//             src={
//               item.imagePaths[0] ? `${item.imagePaths[0]}` : "/placeholder.png"
//             }
//             alt=""
//           />

//           <a href="#">
//             <h5 className="mt-3 mb-2 text-2xl font-semibold tracking-tight text-heading w-60">
//               {item.title}
//             </h5>
//           </a>
//           <p className="mb-3 text-body">{item.artistName || "No info"}</p>

//           <p className="text-body mb-2">{date}</p>

//           <p className="text-body">{label || "No Label info"}</p>

//           <p className="text-body text-xl mt-5 text-green-500">
//             {item.discountedPrice
//               ? item.discountedPrice.toLocaleString("tr-TR")
//               : item.price.toLocaleString("tr-TR")}{" "}
//             ₺
//           </p>
//         </button>
//         {!isOwnerUser && (
//           <div className="flex flex-row gap-3 justify-center">
//             <TradeButton listingId={item.id} />
//             <a
//               onClick={() =>
//                 inCart ? removeFromCart(item.id) : addToCart(item.id)
//               }
//               className={`mt-5 rounded-xl  border   focus:ring-4  shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-4 cursor-pointer min-w-30  hover:text-red-700
//               ${inCart ? "bg-green-700 text-white hover:border-red-700" : ""}`}
//             >
//               {inCart ? "In Cart" : "Add to Cart"}
//             </a>
//           </div>
//         )}
//         {isOwnerUser && (
//           <div className="flex flex-row gap-3 justify-center">
//             <a
//               onClick={() => {
//                 navigateEditItemWithId();
//               }}
//               className="text-body mt-5 rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-20 cursor-pointer"
//             >
//               Edit
//             </a>
//           </div>
//         )}
//       </div>
//     </>
//   );
// }
