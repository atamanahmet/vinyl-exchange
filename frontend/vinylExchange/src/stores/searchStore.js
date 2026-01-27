import { create } from "zustand";
import axios from "axios";

export const useSearchStore = create((set, get) => ({
  search: async (title) => {
    try {
      const res = await axios.get(
        "http://localhost:8080/api/mb/search",
        {
          params: {
            title: title,
            limit: 20,
          },
        },
        {
          withCredentials: true,
        },
      );
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
}));
