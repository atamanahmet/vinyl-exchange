export function watchlistItemToCardItem(
  item,
  isInWishlist,
  removeFromWishlist,
) {
  const inWishlist = isInWishlist(item);

  return {
    id: item.id,
    title: item.title,
    artist: item.artist || "Unknown artist",
    format: item.format || "Unknown format",
    imageUrl: item.imageUrl,
    date: item.year ? item.year : "Unknown Date",
    price: item.price ? item.price : "Unknown Price",
    // label: item.labelInfo?.[0].label?.name || "Unknown Label",

    primaryAction: {
      label: inWishlist ? "Remove" : "Add to Wishlist",

      onClick: async () => {
        const result = await removeFromWishlist(item.id);

        if (result) {
          console.log("Removed to wishlist:", item.title);
        } else {
          console.log("Failed to remove", item.title);
        }
      },
      //   isActive: inWishlist, // To show active state in UI
    },
  };
}
