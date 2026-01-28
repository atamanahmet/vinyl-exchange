import { useState } from "react";

export default function CardImage({ src, alt }) {
  const [loaded, setLoaded] = useState(false);
  const [error, setError] = useState(false);

  return (
    <div className="relative h-60 w-60">
      {/* skeleton */}
      {!loaded && !error && (
        <div className="absolute inset-0 rounded-md bg-neutral-600 animate-pulse" />
      )}

      {/* image */}
      {!error && (
        <img
          src={src}
          alt={alt}
          className={`rounded-md h-60 w-60 object-cover transition-opacity duration-300 ${
            loaded ? "opacity-100" : "opacity-0"
          }`}
          onLoad={() => setLoaded(true)}
          onError={() => {
            setError(true);
            setLoaded(true);
          }}
        />
      )}

      {/* placeholder on error */}
      {error && (
        <img
          src="/placeholder.png"
          alt="placeholder"
          className="rounded-md h-60 w-60 object-cover"
        />
      )}
    </div>
  );
}
