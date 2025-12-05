import { useState, useEffect } from "react";
import "../App.css";
import { ThemeProvider } from "@material-tailwind/react";
import Card from "../comps/Card";
import axios from "axios";

export default function ListingsPage() {
  const [listings, setListings] = useState([]);

  async function fetchListings() {
    try {
      const res = await axios.get("http://localhost:8080/myListings", {
        withCredentials: true,
      });

      setListings(res.data);
    } catch (error) {
      console.log(error);
    }
  }

  useEffect(() => {
    fetchListings();
  }, []);

  return (
    <>
      <div className="grid grid-cols-4 gap-4 mt-20">
        {listings &&
          listings.map((item) => <Card key={item.id} vinyl={item} />)}
      </div>
    </>
  );
}
