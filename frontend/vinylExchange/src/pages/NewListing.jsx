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
    tradeValue: 0,

    tradeable: false,
    price: 0,
    discount: 0,
    tradePreferences: [],
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    setListing((prev) => {
      const updated = {
        ...prev,
        [name]: type === "checkbox" ? checked : value,
      };

      // tradeable disabled, clear prefs
      if (name === "tradeable" && checked === false) {
        updated.tradePreferences = [];
      }

      return updated;
    });
  };

  const handleTradePrefChange = (index, field, value) => {
    setListing((prev) => {
      const prefs = [...prev.tradePreferences];
      prefs[index] = {
        ...prefs[index],
        [field]: value,
      };

      return { ...prev, tradePreferences: prefs };
    });
  };

  const addPreference = () => {
    setListing((prev) => ({
      ...prev,
      tradePreferences: [
        ...prev.tradePreferences,
        {
          desiredItem: "",
          paymentDirection: "NO_EXTRA",
          extraAmount: 0,
        },
      ],
    }));
  };

  const removePreference = (index) => {
    setListing((prev) => ({
      ...prev,
      tradePreferences: prev.tradePreferences.filter((_, i) => i !== index),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      ...listing,
      tradePreferences: listing.tradeable ? listing.tradePreferences : null,
    };

    const formData = new FormData();

    // JSON as string
    formData.append(
      "listing",
      new Blob([JSON.stringify(payload)], {
        type: "application/json",
      })
    );

    // upload images
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
    <div className="">
      <h2 className="text-3xl font-bold text-left mt-15 ml-4 mb-5">
        Create New Listing
      </h2>
      <form onSubmit={handleSubmit} className=" p-4 space-y-4 text-left">
        <div className="grid grid-cols-[0.9fr_0.5fr_1fr_1fr]">
          <div className="">
            <div className="formItem">
              <h3 className="text-3xl font-bold mb-5">Listing information</h3>
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
          <div className="formItem max-w-50 mt-14 ml-4">
            <label className="block mb-1 ">Format</label>

            <div className="flex flex-col gap-2">
              {/* listing LP */}
              <label className="inline-flex items-center gap-2 ">
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
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="format"
                  value="other"
                  checked={listing.format === "Other"}
                  onChange={handleChange}
                  className="radio radio-primary border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
                <span>Other</span>
              </label>
            </div>
          </div>
          <div className="">
            <h3 className="text-3xl font-bold">Trade stuff</h3>
            <div className="mt-5">
              <div className="formItem mt-5">
                <label className="block mb-1">Direct Sell price</label>
                <input
                  type="number"
                  name="price"
                  value={listing.price}
                  onChange={handleChange}
                  className="input w-75 input-bordered  border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
              </div>
              <label className="inline-flex items-center gap-2 mt-5">
                <input
                  type="checkbox"
                  name="tradeable"
                  checked={listing.tradeable}
                  onChange={handleChange}
                  className="border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
                <span>Open to trade</span>
                <div className="vl -mr-0.5"></div>
                <label
                  className={`block 
      ${!listing.tradeable ? "opacity-50 cursor-not-allowed" : ""}`}
                >
                  Trade value
                </label>
                <input
                  type="number"
                  name="tradeValue"
                  value={listing.tradeValue}
                  onChange={handleChange}
                  disabled={!listing.tradeable}
                  className={`input w-19 input-bordered border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1 
      ${!listing.tradeable ? "opacity-50 cursor-not-allowed" : ""}`}
                />
              </label>
              {/* <div className="formItem mt-5">
                <label className="block mb-1">Discount</label>
                <input
                  type="number"
                  name="discount"
                  value={listing.discount}
                  onChange={handleChange}
                  className="input w-75 input-bordered  border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1"
                />
              </div> */}
              {listing.tradeable && (
                <div className="mt-5">
                  <div className=" items-center justify-between mb-2 relative">
                    <label className="font-bold">Trade Preferences</label>

                    <button
                      type="button"
                      onClick={() =>
                        setListing((prev) => ({
                          ...prev,
                          tradePreferences: [
                            ...prev.tradePreferences,
                            {
                              desiredItem: "",
                              price: 0,
                              extraAmount: 0,
                              paymentDirection: "NO_EXTRA",
                            },
                          ],
                        }))
                      }
                      className="btn btn-sm border border-amber-200 absolute right-11 bottom-0.5"
                    >
                      +
                    </button>
                  </div>

                  {listing.tradePreferences.map((pref, index) => (
                    <div
                      key={index}
                      className="mb-3 items-center relative border rounded-md p-1 py-2 -ml-1.5 w-78"
                    >
                      {/* Remove */}
                      <button
                        type="button"
                        onClick={() =>
                          setListing((prev) => ({
                            ...prev,
                            tradePreferences: prev.tradePreferences.filter(
                              (_, i) => i !== index
                            ),
                          }))
                        }
                        className="text-red-500 text-xl text-center absolute right-2 top-1 rounded-full p-0.5 w-7"
                      >
                        x
                      </button>
                      {/* Desired Item */}
                      <input
                        type="text"
                        placeholder="Record title"
                        value={pref.desiredItem}
                        onChange={(e) =>
                          handleTradePrefChange(
                            index,
                            "desiredItem",
                            e.target.value
                          )
                        }
                        className="input w-75 input-bordered border-2 border-amber-50 ring-1 ring-indigo-800 rounded-md pl-2 py-1 mb-3"
                      />

                      <div className=" mb-1">
                        <label className="font-bold">Value difference</label>
                      </div>

                      <div className="flex gap-2">
                        <button
                          type="button"
                          onClick={() =>
                            handleTradePrefChange(
                              index,
                              "paymentDirection",
                              "NO_EXTRA"
                            )
                          }
                          className={`px-2 py-1 rounded-md border ${
                            pref.paymentDirection === "NO_EXTRA"
                              ? "bg-green-600 text-white"
                              : "bg-indigo-700"
                          }`}
                        >
                          0
                        </button>

                        <button
                          type="button"
                          onClick={() =>
                            handleTradePrefChange(
                              index,
                              "paymentDirection",
                              "PAY"
                            )
                          }
                          className={`px-2 py-1 rounded-md border ${
                            pref.paymentDirection === "PAY"
                              ? "bg-red-600 text-white"
                              : "bg-indigo-700"
                          }`}
                        >
                          Pay
                        </button>

                        <button
                          type="button"
                          onClick={() =>
                            handleTradePrefChange(
                              index,
                              "paymentDirection",
                              "RECEIVE"
                            )
                          }
                          className={`px-2 py-1 rounded-md border ${
                            pref.paymentDirection === "RECEIVE"
                              ? "bg-green-600 text-white"
                              : "bg-indigo-700"
                          }`}
                        >
                          Receive
                        </button>
                        {/* Extra Amount */}
                        <input
                          type="number"
                          placeholder="Amount"
                          value={pref.extraAmount}
                          onChange={(e) =>
                            handleTradePrefChange(
                              index,
                              "extraAmount",
                              e.target.value
                            )
                          }
                          disabled={pref.paymentDirection === "NO_EXTRA"}
                          className={`input input-bordered border-3 rounded-md w-33.5 border-white pl-1.5 ${
                            pref.paymentDirection === "NO_EXTRA"
                              ? "opacity-50 cursor-not-allowed"
                              : ""
                          }`}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
          <div>
            <h3 className="text-3xl font-bold mb-5">Upload images</h3>

            <ImageUploader images={images} setImages={setImages} />
          </div>
        </div>
      </form>
    </div>
  );
}
