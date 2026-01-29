export function mbReleaseToCardItem(release, isInWishlist, toggleWishlist) {
  const inWishlist = isInWishlist(
    release.title,
    release.artistCredit?.[0]?.name || "Unknown artist",
  );

  return {
    id: release.id,
    title: release.title,
    artist: release.artistCredit?.[0]?.name || "Unknown artist",
    format: release.media?.[0]?.format || "Unknown format",
    imageUrl: release.imagePaths,
    year: release.date ? release.date.substring(0, 4) : null,
    label: release.label,
    imageUrl: `https://coverartarchive.org/release/${release.id}/front-250`,

    primaryAction: {
      label: inWishlist ? "Remove from Wishlist" : "Add to Wishlist",
      onClick: async () => {
        const result = await toggleWishlist({
          title: release.title,
          artist: release.artistCredit?.[0]?.name || "Unknown artist",
          year: release.date ? release.date.substring(0, 4) : null,
          format: release.media?.[0]?.format || "Unknown format",
          imageUrl: release.imagePaths,
        });

        if (result === null) {
          console.log("Failed to toggle wishlist for:", release.title);
        } else if (result) {
          console.log("Added to wishlist:", release.title);
        } else {
          console.log("Removed from wishlist:", release.title);
        }
      },
      isActive: inWishlist, // To show active state in UI
    },
  };
}
