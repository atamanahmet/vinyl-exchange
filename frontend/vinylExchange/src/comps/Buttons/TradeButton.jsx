import { useAuthStore } from "../../stores/authStore";
import { useMessagingStore } from "../../stores/messagingStore";

export default function TradeButton({ listingId }) {
  const user = useAuthStore((state) => state.user);
  const startConversation = useMessagingStore(
    (state) => state.startConversation,
  );

  const navigateMessagingWithItemId = async () => {
    console.log("item id to start convo: " + listingId);
    await startConversation(listingId);
  };

  return (
    <>
      <a
        onClick={() => {
          navigateMessagingWithItemId();
        }}
        className="text-body mt-5 cursor-pointer rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-9"
      >
        Trade
      </a>
    </>
  );
}
