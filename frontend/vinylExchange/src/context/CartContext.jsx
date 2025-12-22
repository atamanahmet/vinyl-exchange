import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";

const CartContext = createContext();

export function CartProvider({ children }) {
  const [cart, setCart] = useState({
    cartId: "",
    items: [],
    price: 0,
    totalItems: 0,
    validationIssues: [],
  });
  const [cartItemCount, setCartItemCount] = useState(0);

  async function fetchCart() {
    try {
      const res = await axios.get("http://localhost:8080/api/cart", {
        withCredentials: true,
      });
      if (res.status === 200) {
        setCart(res.data);
        setCartItemCount(res.data.items.length);
      }
    } catch (e) {
      console.log(e);
    }
  }

  useEffect(() => {
    fetchCart();
  }, []);

  async function addToCart(id) {
    try {
      const res = await axios.post(
        "http://localhost:8080/api/cart/items",
        { listingId: id, quantity: 1 },
        { withCredentials: true }
      );
      if (res.status === 200) {
        // refresh shared cart after change
        fetchCart();
      }
    } catch (e) {
      console.log(e);
    }
  }
  async function decreaseFromCart(id) {
    try {
      const res = await axios.patch(
        `http://localhost:8080/api/cart/items/${id}`,
        { withCredentials: true }
      );
      if (res.status === 200) {
        // refresh shared cart after change
        fetchCart();
      }
    } catch (e) {
      console.log(e);
    }
  }

  return (
    <CartContext.Provider
      value={{
        cart,
        cartItemCount,
        fetchCart,
        addToCart,
        decreaseFromCart,
        setCart,
        setCartItemCount,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
