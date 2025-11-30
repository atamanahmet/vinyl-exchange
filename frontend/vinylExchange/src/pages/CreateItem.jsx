import { useState } from "react";
import axios from "axios";

export default function CreateItem() {
  const [vinyl, setVinyl] = useState({
    id: "",
    title: "",
    status: "",
    date: "",
    country: "",
    barcode: "",
    packaging: "",
    format: "",
    trackCount: 0,
    coverUrl: "",
    artistName: "",
    artistId: "",
    labelId: "", // for ManyToOne Label
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setVinyl((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8080/vinyl", vinyl, {
        withCredentials: true,
      });
      console.log("Vinyl created:", res.data);
      alert("Vinyl added successfully!");
    } catch (err) {
      console.error(err);
      alert("Error adding vinyl");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="max-w-xl mx-auto p-4 space-y-4">
      <div>
        <label className="block mb-1">ID</label>
        <input
          type="text"
          name="id"
          value={vinyl.id}
          onChange={handleChange}
          className="input input-bordered w-full"
          required
        />
      </div>

      <div>
        <label className="block mb-1">Title</label>
        <input
          type="text"
          name="title"
          value={vinyl.title}
          onChange={handleChange}
          className="input input-bordered w-full"
          required
        />
      </div>

      <div>
        <label className="block mb-1">Status</label>
        <input
          type="text"
          name="status"
          value={vinyl.status}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Release Date</label>
        <input
          type="date"
          name="date"
          value={vinyl.date}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Country</label>
        <input
          type="text"
          name="country"
          value={vinyl.country}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Barcode</label>
        <input
          type="text"
          name="barcode"
          value={vinyl.barcode}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Packaging</label>
        <input
          type="text"
          name="packaging"
          value={vinyl.packaging}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Format</label>
        <input
          type="text"
          name="format"
          value={vinyl.format}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Track Count</label>
        <input
          type="number"
          name="trackCount"
          value={vinyl.trackCount}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Cover URL</label>
        <input
          type="text"
          name="coverUrl"
          value={vinyl.coverUrl}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Artist Name</label>
        <input
          type="text"
          name="artistName"
          value={vinyl.artistName}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Artist ID</label>
        <input
          type="text"
          name="artistId"
          value={vinyl.artistId}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <div>
        <label className="block mb-1">Label ID</label>
        <input
          type="text"
          name="labelId"
          value={vinyl.labelId}
          onChange={handleChange}
          className="input input-bordered w-full"
        />
      </div>

      <button type="submit" className="btn btn-primary w-full">
        Add Vinyl
      </button>
    </form>
  );
}
