import { useEffect, useState } from "react";
import axios from "axios";

export default function ListingItem({ item }) {
  const [image, setImage] = useState("");

  return (
    <>
      <div className="bg-neutral-primary-soft border-b  border-default grid grid-cols-6 items-center">
        <img
          src={`http://localhost:8080/${item.imagePaths[0]}`}
          alt="listing main image"
        />
        <p
          scope="row"
          className="px-6 py-4 font-medium text-heading whitespace-nowrap"
        >
          {item.title}
        </p>
        <p className="px-6 py-4">{item.date} </p>
        <p className="px-6 py-4">{item.format} </p>
        {/* <p className="px-6 py-4">{item.trackCount}</p> */}

        <p className="px-6 py-4">{item.price}</p>
        <p className="px-6 py-4 text-right">
          <a href="#" className="font-medium text-fg-brand hover:underline">
            Edit
          </a>
        </p>
      </div>
    </>
  );
}
