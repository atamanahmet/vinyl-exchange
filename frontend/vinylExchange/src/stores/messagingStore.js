import { create } from "zustand";
import axios from "axios";
import { useAuthStore } from "./authStore";
import { useUIStore } from "./uiStore";
import { navigate } from "../utils/router";

export const useMessagingStore = create((set, get) => ({
  activeConvoId: null,
  unreadCount: null,

  setActiveConvoId: (id) => set({ activeConvoId: id }),

  fetchUnreadCount: async () => {
    const user = useAuthStore.getState().user;
    try {
      const response = await axios.get(
        "http://localhost:8080/api/messages/unread",
        { withCredentials: true },
      );

      set({ unreadCount: response.data.unreadCount });
    } catch (error) {
      console.error("Failed to fetch unread count:", error);
    }
    // if (user) {

    // }
  },
  startConversation: async (relatedListingId) => {
    const user = useAuthStore.getState().user;

    if (!user) {
      const isLoggedIn = await useUIStore.getState().waitForLogin();
      if (!isLoggedIn) {
        return;
      }
    }

    try {
      const res = await axios.post(
        `http://localhost:8080/api/messages/start`,
        { relatedListingId: relatedListingId },
        { withCredentials: true },
      );

      if (res.status == 201) {
        console.log("convo started");
        set({ activeConvoId: res.data.id });
        navigate(`/messaging/${relatedListingId}`);
        return true;
      }

      console.warn("Unexpected convo starting status:", res.status);
      return false;
    } catch (error) {
      console.log("convo starting error " + error);
      return false;
    }
  },
}));
