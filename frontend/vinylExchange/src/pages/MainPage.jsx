import { useState, useEffect } from "react";
import "../App.css";
import { ThemeProvider } from "@material-tailwind/react";
import Card from "../comps/Card";

export default function MainPage({ data }) {
  return (
    <>
      <div className="fixedRoot flex gap-5 flex-row flex-wrap  w-350 mt-15 ">
        {data && data.map((item) => <Card key={item.id} vinyl={item} />)}
      </div>
    </>
  );
}
