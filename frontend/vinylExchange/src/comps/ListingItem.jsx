import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function ListingItem({ item, onDelete }) {
  const [image, setImage] = useState("");
  const navigate = useNavigate();
  const navigateToEditWithId = () => {
    navigate("/edit", { state: { id: item.id } });
  };

  return (
    <>
      <div className="bg-neutral-primary-soft pb-5 border-b gap-5 border-default grid grid-cols-6 items-center">
        <img
          src={`http://localhost:8080/${item.imagePaths[0]}`}
          onError={(e) => {
            e.target.src = "/placeholder.png";
          }}
          alt="listing main image"
          className="bg-black"
        />
        <p
          scope="row"
          className="px-6 py-4 font-medium text-heading flex flex-col overflow-auto "
        >
          {item.title}
        </p>
        <p className="px-6 py-4">{item.date} </p>
        <p className="px-6 py-4">{item.format} </p>
        {/* <p className="px-6 py-4">{item.trackCount}</p> */}

        <p className="px-6 py-4">{item.price}</p>

        <div className="px-6 py-4 text-right">
          <div className="flex flex-col justify-center items-center -mt-5">
            <a
              onClick={() => {
                navigateToEditWithId();
              }}
              className="font-medium text-fg-brand hover:underline bg-indigo-700 py-2 px-4.5 mb-2 rounded-md  cursor-pointer"
            >
              Edit
            </a>
            <a
              onClick={() => onDelete(item.id)}
              className="font-medium text-fg-brand bg-red-700 p-2 rounded-md hover:underline cursor-pointer"
            >
              Delete
            </a>
          </div>
        </div>
      </div>
    </>
  );
}
