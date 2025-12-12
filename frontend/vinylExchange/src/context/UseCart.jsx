import axios from "axios";
import { useState } from "react";

export function useCart() {
  const [cart, setCart] = useState([]);

  async function fetchCart() {
    try {
      const res = await axios.get("http://localhost:8080/cart", {
        withCredentials: true,
      });
      if (res.status === 200) {
        console.log(res.data);
        setCart(res.data);
      }
    } catch (error) {
      console.log(error);
    }
  }

  const addToCart = (id) => {
    setCart((prev) => {
      const item = prev.find((p) => p.id === id);

      if (item) {
        return prev.map((p) => (p.id === id ? { ...p, qty: p.qty + 1 } : p));
      }

      return [...prev, { id, qty: 1 }];
    });
  };

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
  };
}
