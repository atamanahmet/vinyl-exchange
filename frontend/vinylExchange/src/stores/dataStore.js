import axios from "axios";
import { create } from "zustand";

export const useDataStore = create((set, get) => ({
  isFetching: false,
  data: null,
  dataType: "",
  hasError: false,
  avatar: sessionStorage.getItem("avatar") || null,
  searchQuery: "",

  setSearchQuery: (query) => set({ searchQuery: query }),

  fetchAllListings: async () => {
    if (get().isFetching) return;

    set({ isFetching: true });

    const url = "http://localhost:8080/api/listings";

    try {
      const res = await axios.get(url, {
        withCredentials: true,
      });

      if (res.status === 200) {
        set({
          data: res.data,
          dataType: "listing",
          hasError: false,
        });
        return true;
      }
      console.warn("Unexpected data fetch status:", res.status);
      return false;
    } catch (err) {
      console.error("Backend error:", err);
      set({
        data: null,
        hasError: true,
      });
      return false;
    } finally {
      set({ isFetching: false });
    }
  },
  search: async (query) => {
    if (!query) {
      await get().fetchAllListings();
      return;
    }

    const url = "http://localhost:8080/api/mb/search";

    try {
      const res = await axios.get(url, {
        params: {
          title: query,
          limit: 20,
        },
        withCredentials: true,
      });

      if (res.status === 200) {
        set({ data: res.data, dataType: "mb" });
        console.log(res.data);
      } else {
        console.error("Search failed with status:", res.status);
      }
    } catch (err) {
      console.log("Search error:", err);
    }
  },
}));
