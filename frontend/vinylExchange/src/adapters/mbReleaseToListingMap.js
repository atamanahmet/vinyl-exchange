export function mbReleaseToListingMap(release) {
  return {
    id: release.id,
    title: release.title,
    artist: release.artistCredit?.[0]?.name || "Unknown artist",
    format: release.media?.[0]?.format || "Unknown format",
    imageUrl: release.imageUrl,
    country: release.country,
    barcode: release.barcode,
    date: release.date ? release.date.substring(0, 4) : "Unknown Date",
    label: release.labelInfo?.[0].label?.name || "Unknown Label",
  };
}
