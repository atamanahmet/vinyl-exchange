import { useState } from "react";
import axios from "axios";
import ImageUploader from "../comps/ImageUploader";

export default function NewListing() {
  const [images, setImages] = useState([]);
  const [listing, setListing] = useState({
    title: "",
    status: "",
    date: "",
    country: "",
    barcode: "",
    packaging: "",
    format: "",
    trackCount: 0,
    artistName: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setListing((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();

    // JSON as string
    formData.append(
      "listing",
      new Blob([JSON.stringify(listing)], {
        type: "application/json",
      })
    );

    // images
    images.forEach((img) => {
      formData.append("images", img);
    });
    for (let pair of formData.entries()) {
      console.log(pair[0], pair[1]);
    }

    try {
      const res = await axios.post(
        "http://localhost:8080/newlisting",
        formData,
        {
          withCredentials: true,
          headers: { "Content-Type": "multipart/form-data" },
        }
      );

      alert("Listing added successfully!");
    } catch (err) {
      console.error(err);
      alert("Error adding vinyl");
    }
  };

  return (
    <div className="-ml-40">
      <h2 className="text-3xl font-bold text-left mt-15 ml-4 mb-5">
        Create New Listing
      </h2>
      <form onSubmit={handleSubmit} className=" p-4 space-y-4 text-left">
        <div className="grid grid-cols-[0.9fr_0.5fr_1fr]">
          <div className="">
            <div className="formItem">
              <label className="block mb-1">Title</label>
              <input
                type="text"
                name="title"
                value={listing.title}
                onChange={handleChange}
                className="input w-75 input-bordered border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                required
              />
            </div>

            <div className="formItem">
              <label className="block mb-1">Status</label>
              <input
                type="text"
                name="status"
                value={listing.status}
                onChange={handleChange}
                className="input w-75 input-bordered border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
              />
            </div>

            <div className="formItem">
              <label className="block mb-1">Release Date</label>
              <input
                type="date"
                name="date"
                value={listing.date}
                onChange={handleChange}
                className="input w-75 input-bordered  border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
              />
            </div>

            <div className="formItem">
              <label className="block mb-1">Country</label>
              <input
                type="text"
                name="country"
                value={listing.country}
                onChange={handleChange}
                className="input w-75 input-bordered border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
              />
            </div>

            <div className="formItem">
              <label className="block mb-1">Barcode</label>
              <input
                type="text"
                name="barcode"
                value={listing.barcode}
                onChange={handleChange}
                className="input w-75 input-bordered  border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
              />
            </div>

            <div className="formItem">
              <label className="block mb-1">Packaging</label>
              <input
                type="text"
                name="packaging"
                value={listing.packaging}
                onChange={handleChange}
                className="input w-75 input-bordered  border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
              />
            </div>

            <div className="formItem">
              <label className="block mb-1">Track Count</label>
              <input
                type="number"
                name="trackCount"
                value={listing.trackCount}
                onChange={handleChange}
                className="input w-75 input-bordered  border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
              />
            </div>

            <div className="formItem">
              <label className="block mb-1">Artist Name</label>
              <input
                type="text"
                name="artistName"
                value={listing.artistName}
                onChange={handleChange}
                className="input w-75 input-bordered border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1 mt-5"
            >
              Create
            </button>
          </div>
          <div className="formItem max-w-50">
            <label className="block mb-1">Format</label>

            <div className="flex flex-col gap-2 mt-1">
              {/* listing LP */}
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="format"
                  value="33"
                  checked={listing.format === "33"}
                  onChange={handleChange}
                  className="radio radio-primary border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
                <span>LP 12"/ 33 </span>
              </label>

              {/* 7" Single */}
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="format"
                  value="45"
                  checked={listing.format === "45"}
                  onChange={handleChange}
                  className="radio radio-primary border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
                <span>Single 7" / 45 </span>
              </label>

              {/* EP */}
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="format"
                  value="EP"
                  checked={listing.format === "EP"}
                  onChange={handleChange}
                  className="radio radio-primary border-2  border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
                <span>EP</span>
              </label>

              {/* Cassette */}
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="format"
                  value="Cassette"
                  checked={listing.format === "Cassette"}
                  onChange={handleChange}
                  className="radio radio-primary border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
                <span>Cassette</span>
              </label>

              {/* CD */}
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="format"
                  value="CD"
                  checked={listing.format === "CD"}
                  onChange={handleChange}
                  className="radio radio-primary border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
                <span>CD</span>
              </label>
            </div>
          </div>
          <ImageUploader images={images} setImages={setImages} />
        </div>
      </form>
    </div>
  );
}
