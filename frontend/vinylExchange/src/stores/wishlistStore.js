import { create } from "zustand";
import axios from "axios";

const useWishlistStore = create((set, get) => ({
  wishlistItems: [],
  isLoading: false,
  error: null,

  fetchWishlist: async () => {
    set({ isLoading: true, error: null });

    try {
      const res = await axios.get("http://localhost:8080/api/wishlists", {
        withCredentials: true,
      });

      if (res.status === 200) {
        set({ wishlistItems: res.data, isLoading: false });
        console.log(res.data);
        return true;
      }
      // exist
      console.warn("Unexpected fetch wishlist status:", res.status);
      set({ isLoading: false });
      return false;
    } catch (error) {
      console.log("Error fetching wishlist:", error);
      set({ error: "Failed to fetch wishlist", isLoading: false });
      return false;
    }
  },

  addToWishlist: async (release) => {
    set({ isLoading: true, error: null });

    console.log("wish release: ", release);

    const url = "http://localhost:8080/api/wishlists";

    try {
      const res = await axios.post(
        url,
        {
          title: release.title,
          artist: release.artist,
          year: release.year,
          format: release.format,
          imageUrl: release.imageUrl,
        },

        { withCredentials: true },
      );
      if (res.status === 201) {
        return true;
      }

      console.warn("Unexpected wishlist post status:", res.status);
      return false;
    } catch (error) {
      console.log(error);

      return false;
    }
  },

  removeFromWishlist: async (wishlistId) => {
    set({ isLoading: true, error: null });
    const url = `http://localhost:8080/api/wishlists/${wishlistId}`;

    try {
      const res = await axios.delete(url, {
        withCredentials: true,
      });

      if (res.status === 204) {
        set((state) => ({
          wishlistItems: state.wishlistItems.filter(
            (item) => item.id !== wishlistId,
          ),
          isLoading: false,
        }));
        return true;
      }

      console.warn("Unexpected remove wishlist status: ", res.status);
      set({ isLoading: false });

      return false;
    } catch (error) {
      console.log("Error removing from wishlist: ", error);
      set({
        error: "Failed to remove from wishlist",
        isLoading: false,
      });
      return false;
    }
  },

  isInWishlist: (title, artist) => {
    const { wishlistItems } = get();
    return wishlistItems.some(
      (item) =>
        item.title.toLowerCase() === title.toLowerCase() &&
        item.artist.toLowerCase() === artist.toLowerCase(),
    );
  },

  toggleWishlist: async (release) => {
    const { isInWishlist, addToWishlist, removeByTitleAndArtist } = get();

    if (isInWishlist(release.title, release.artist)) {
      await removeByTitleAndArtist(release.title, release.artist);
      return false;
    } else {
      await addToWishlist(release);
      return true;
    }
  },

  clearError: () => set({ error: null }),

  reset: () => set({ wishlistItems: [], isLoading: false, error: null }),
}));

export default useWishlistStore;
