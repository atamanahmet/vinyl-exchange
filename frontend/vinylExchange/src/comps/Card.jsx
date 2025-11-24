export default function Card({ vinyl }) {
  const coverUrl = `https://coverartarchive.org/release/${vinyl.id}/front-250`;
  let date;
  let label;
  if (vinyl.date) {
    date = vinyl.date.substring(0, 4);
  } else {
    date = "No date";
  }
  if (vinyl.label) {
    if (vinyl.label.name.includes("no label")) {
      label = "";
    } else {
      if (vinyl.label.name.includes("Records")) {
        console.log("includes");
        label = vinyl.label.name;
      } else {
        console.log("notincludes");

        label = vinyl.label.name + " Records";
      }
    }
  }
  return (
    <div className="bg-neutral-primary-soft  max-w-sm p-6 border border-default rounded-2xl shadow-xs flex flex-col justify-between">
      <img
        className="rounded-xl h-60 w-60"
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
      <p className="mb-3 text-body">{vinyl.artistName || "No info"}</p>

      <p className="text-body mb-2">{date}</p>

      <p className="text-body">{label || "No Label info"}</p>

      <div className="flex flex-row gap-3 justify-center">
        <a
          href="#"
          className="text-body mt-5 rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-9"
        >
          Trade
        </a>
        <a
          href="#"
          className="text-body mt-5 rounded-xl bg-neutral-secondary-medium box-border border border-default-medium hover:bg-neutral-tertiary-medium hover:text-heading focus:ring-4 focus:ring-neutral-tertiary shadow-xs font-medium leading-5  text-sm  py-2.5 focus:outline-none text-center px-10"
        >
          Buy
        </a>
      </div>
    </div>
  );
}
