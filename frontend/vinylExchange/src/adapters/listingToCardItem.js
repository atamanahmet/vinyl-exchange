export function listingToCardItem(
  listing,
  user,
  cart,
  navigate,
  addToCart,
  removeFromCart,
) {
  const inCart = cart?.items?.some((i) => i.listingId === listing.id);

  return {
    id: listing.id,
    title: listing.title,
    artist: listing.artistName,
    year: listing.date ? listing.date.substring(0, 4) : null,
    label: listing.labelName,
    imageUrl: listing.imagePaths?.[0] || "/placeholder.png",
    price: listing.price,
    discountedPrice: listing.discountedPrice,
    isOwned: user?.username === listing.ownerUsername,
    isInCart: inCart,

    primaryAction: {
      label: "View",
      onClick: () => navigate(`/listing/${listing.id}`),
    },

    secondaryAction:
      user?.username !== listing.ownerUsername
        ? {
            label: inCart ? "In Cart" : "Add to Cart",
            onClick: () =>
              inCart ? removeFromCart(listing.id) : addToCart(listing.id),
          }
        : {
            label: "Edit",
            onClick: () => navigate(`/edit/${listing.id}`),
          },
  };
}
