import { create } from "zustand";
import axios from "axios";
import { useAuthStore } from "./authStore";
import { useUIStore } from "./uiStore";
import { navigate } from "../utils/router";

export const useMessagingStore = create((set, get) => ({
  activeConvoId: null,

  setActiveConvoId: (id) => set({ activeConvoId: id }),

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
