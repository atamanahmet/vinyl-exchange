import axios from "axios";
import { useEffect, useState } from "react";

export function useCart() {
  const [cart, setCart] = useState([]);
  const [cartItemCount, setCartItemCount] = useState();

  async function fetchCart() {
    try {
      const res = await axios.get("http://localhost:8080/cart/items", {
        withCredentials: true,
      });
      if (res.status === 200) {
        // console.log("cart response: " + res.data.cartItems[0].id);
        setCartItemCount(res.data.cartItems.length);
        setCart(res.data.cartItems);
      }
    } catch (error) {
      console.log(error);
    }
  }
  useEffect(() => {
    fetchCart();
  }, []);

  // async function handleCart() {
  //   try {
  //     const res = await axios.post(
  //       "http://localhost:8080/cart/items",
  //       { listingId: id, quantity: 1 },
  //       {
  //         withCredentials: true,
  //       }
  //     );
  //     if (res.status === 200) {
  //       console.log("cart response: " + res.data);
  //       setCartItemCount(res.data.length);
  //       setCart(res.data);
  //     }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // }

  async function addToCart(id) {
    try {
      const res = await axios.post(
        "http://localhost:8080/cart/items",
        { listingId: id, quantity: 1 },
        {
          withCredentials: true,
        }
      );
    } catch (error) {
      console.log(error);
    }
  }

  // if 0 remove
  const decreaseFromCart = (id) => {
    setCart((prev) => {
      const item = prev.find((p) => p.id === id);
      if (!item) return prev;

      if (item.qty === 1) {
        return prev.filter((p) => p.id !== id);
      }

      return prev.map((p) => (p.id === id ? { ...p, qty: p.qty - 1 } : p));
    });
  };

  const removeFromCart = (id) => {
    setCart((prev) => prev.filter((p) => p.id !== id));
  };

  const clearCart = () => {
    setCart([]);
  };

  return {
    cart,
    addToCart,
    decreaseFromCart,
    removeFromCart,
    clearCart,
    cartItemCount,
  };
}
