import { useState, useEffect } from "react";
import "../App.css";
import { ThemeProvider } from "@material-tailwind/react";
import Card from "../comps/Card";
import { useCart } from "../context/CartContext";

export default function MainPage({ data }) {
  const { cart } = useCart();

  // function isInCart(itemId) {
  //   return cart.items.some((element) => element.listingId == itemId);
  // }

  return (
    <>
      <div className="fixedRoot flex gap-5 flex-row flex-wrap  w-350 mt-15 ">
        {data && data.map((item) => <Card key={item.id} vinyl={item} />)}
      </div>
    </>
  );
}
