import { useState, useEffect } from "react";
import React from "react";
import "../App.css";
import { ThemeProvider } from "@material-tailwind/react";
import Card from "../comps/Card";
import axios from "axios";
import ListingItem from "../comps/ListingItem";

export default function ListingsPage() {
  const [listings, setListings] = useState([]);

  async function fetchListings() {
    try {
      const res = await axios.get("http://localhost:8080/api/listings/my", {
        withCredentials: true,
      });
      setListings(res.data);
    } catch (error) {
      console.log(error);
    }
  }

  const deleteListing = async (id) => {
    const res = await axios.delete(`http://localhost:8080/api/listings/${id}`, {
      withCredentials: true,
    });

    if (res.status == 204) {
      fetchListings();
    }
  };

  useEffect(() => {
    fetchListings();
  }, []);

  return (
    <>
      <div className="min-h-screen max-w-7xl mx-auto min-w-300 bg-black text-white">
        <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 pt-4">
          <div className="">
            <h2 className="text-3xl font-semibold mb-5">My listings</h2>
          </div>
          <div className="bg-neutral-primary-soft border-b  border-default grid grid-cols-6 items-center">
            <p>Cover</p>
            <p>Title</p>
            <p>Release Date</p>
            <p>Format</p>
            <p>Price</p>
          </div>
          <div className="mt-6">
            {listings &&
              listings.map((item) => (
                <ListingItem
                  key={item.id}
                  item={item}
                  onDelete={deleteListing}
                />
              ))}
          </div>
        </main>
      </div>
    </>
  );
}
