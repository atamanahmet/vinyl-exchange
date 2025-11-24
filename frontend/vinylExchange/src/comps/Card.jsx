export default function Card({ vinyl }) {
  const coverUrl = `https://coverartarchive.org/release/${vinyl.id}/front-250`;

  return (
    <div className="bg-neutral-primary-soft block max-w-sm p-6 border border-default rounded-base shadow-xs">
      <img
        className="rounded-base"
        src={`https://coverartarchive.org/release/${vinyl.id}/front`}
        onError={(e) => {
          e.target.src = "/placeholder.png";
        }}
        alt=""
      />

      <a href="#">
        <h5 className="mt-3 mb-2 text-2xl font-semibold tracking-tight text-heading">
          {vinyl.title}
        </h5>
      </a>
      <p className="mb-3 text-body">
        {vinyl["artist-credit"][0].name || "Default info"}
      </p>
      <p className="text-body">{vinyl.date || "Default info"}</p>
      <a
        href="#"
        className="inline-flex items-center text-body mt-5 bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5 rounded-base text-sm px-4 py-2.5 focus:outline-none"
      >
        Buy
      </a>
    </div>
  );
}
