import { create } from "zustand";
import axios from "axios";

export const useCartStore = create((set, get) => ({
  cart: null,
  cartItemCount: 0,

  fetchCart: async () => {
    try {
      const res = await axios.get("http://localhost:8080/api/cart", {
        withCredentials: true,
      });
      if (res.status === 200) {
        set({
          cart: res.data,
          cartItemCount: res.data.items.length,
        });
      }
    } catch (e) {
      console.log(e);
    }
  },

  addtoCart: async (listingId) => {
    try {
      const res = await axios.post(
        "http://localhost:8080/api/cart/items",
        { listingId: listingId, quantity: 1 },
        { withCredentials: true },
      );
      if (res.status === 200) {
        // refresh cart after change
        await get().fetchCart();
      }
    } catch (e) {
      console.log(e);
    }
  },

  decreaseFromCart: async (cartItemId) => {
    try {
      const res = await axios.patch(
        `http://localhost:8080/api/cart/items/${cartItemId}`,
        {},
        { withCredentials: true },
      );
      if (res.status === 200) {
        // refresh cart after change
        await get().fetchCart();
      }
    } catch (e) {
      console.log(e);
    }
  },

  removeFromCart: async (cartItemId) => {
    try {
      const res = await axios.delete(
        `http://localhost:8080/api/cart/items/${cartItemId}`,
        { withCredentials: true },
      );
      if (res.status === 204) {
        // refresh shared cart after change
        await get().fetchCart();
      }
    } catch (e) {
      console.log(e);
    }
  },
}));
