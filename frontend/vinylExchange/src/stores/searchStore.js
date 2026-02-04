import { create } from "zustand";
import axios from "axios";

export const useSearchStore = create((set, get) => ({
  searchResult: [],
  isLoadingSearch: true,

  searchMusicBrainz: async (title) => {
    set({
      isLoadingSearch: true,
    });

    try {
      const res = await axios.get("http://localhost:8080/api/mb/search", {
        params: {
          title: title,
          limit: 75,
        },
        withCredentials: true,
      });
      if (res.status === 200) {
        console.log("search results: ", res.data);
        set({
          searchResult: res.data,
        });
      }
    } catch (e) {
      console.log(e);
    } finally {
      set({
        isLoadingSearch: false,
      });
    }
  },
  searchProducts: async (title) => {
    set({
      isLoadingSearch: true,
    });

    try {
      const res = await axios.get("http://localhost:8080/api/mb/search", {
        params: {
          title: title,
          limit: 75,
        },
      });
      if (res.status === 200) {
        console.log("search results: ", res.data);
        set({
          searchResult: res.data,
        });
      }
    } catch (e) {
      console.log(e);
    } finally {
      set({
        isLoadingSearch: false,
      });
    }
  },
}));
