import { useCartStore } from "../stores/cartStore";

export function listingToCardItem(
  listing,
  user,
  cartIds,
  addToCart,
  removeFromCart,
  navigate,
  startConversation,
) {
  const isOwner = user?.username === listing.ownerUsername;
  const inCart = cartIds.has(listing.id);

  return {
    id: listing.id,
    title: listing.title,
    artist: listing.artistName,
    date: listing.date ? listing.date.substring(0, 4) : null,
    format: listing.format,
    label: listing.labelName,
    imageUrl: listing.imagePaths?.[0] || "/placeholder.png",
    price: listing.price,
    discountedPrice: listing.discountedPrice,
    isOwned: user?.username === listing.ownerUsername,
    isInCart: inCart,
    isOwner: isOwner,

    primaryAction:
      user?.username !== listing.ownerUsername
        ? {
            label: "Trade",
            onClick: () => startConversation(listing.id),
          }
        : null,

    secondaryAction:
      user?.username !== listing.ownerUsername
        ? {
            label: inCart ? "In Cart" : "Add to Cart",
            onClick: () => addToCart(listing.id),
          }
        : {
            label: "Edit",
            onClick: () => navigate(`/edit/${listing.id}`),
          },
  };
}
