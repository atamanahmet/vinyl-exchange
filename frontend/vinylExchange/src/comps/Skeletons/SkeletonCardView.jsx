export default function SkeletonCardView() {
  return (
    <div className="bg-neutral-primary-soft p-4 border border-default rounded-2xl shadow-xs flex flex-col justify-center">
      <div className="bg-neutral-700 rounded-md h-60 w-60 mb-3 animate-pulse"></div>

      <div className="bg-neutral-700 h-7 rounded w-48 mb-2 animate-pulse"></div>

      <div className="bg-neutral-700 h-4 rounded w-32 mb-3 animate-pulse"></div>

      <div className="bg-neutral-700 h-4 rounded w-16 mb-2 animate-pulse"></div>

      <div className="bg-neutral-700 h-4 rounded w-40 mb-2 animate-pulse"></div>

      <div className="bg-neutral-700 h-6 rounded w-24 mt-5 mb-5 animate-pulse"></div>

      <div className="flex flex-row gap-3 justify-center">
        <div className="bg-neutral-700 h-10 rounded-xl w-28 animate-pulse"></div>
        <div className="bg-neutral-700 h-10 rounded-xl w-32 animate-pulse"></div>
      </div>
    </div>
  );
}
