export function mbReleaseToCardItem(release, navigate) {
  return {
    id: release.id,
    title: release.title,
    artist: release.artistCredit?.[0]?.name || "Unknown artist",
    format: release.media?.[0]?.format || "Unknown format",
    year: release.date ? release.date.substring(0, 4) : null,
    label: release.label,
    imageUrl: `https://coverartarchive.org/release/${release.id}/front-250`,

    primaryAction: {
      label: "Add to Wishlist",
      onClick: () => console.log("add to favorites: " + release),
    },
  };
}
