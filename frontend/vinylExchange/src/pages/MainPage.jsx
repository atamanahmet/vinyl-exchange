import { useState, useEffect } from "react";
import "../App.css";
import { ThemeProvider } from "@material-tailwind/react";
import Card from "../comps/Card";

export default function MainPage({ data }) {
  console.log(data);
  return (
    <>
      <div className="grid grid-cols-4 gap-4 mt-20">
        {data && data.map((item) => <Card key={item.id} vinyl={item} />)}
      </div>
    </>
  );
}
