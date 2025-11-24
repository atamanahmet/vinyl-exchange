import { useState, useEffect } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./App.css";
import { CardDefault } from "./comps/CardDefault";
import { ThemeProvider } from "@material-tailwind/react";
import Card from "./comps/Card";
import axios from "axios";
import Navbar from "./comps/Navbar";

function App() {
  const [count, setCount] = useState(0);
  const [result, setResult] = useState();
  const [isFetching, setIsFetching] = useState();

  const fetchData = async () => {
    if (isFetching) return;
    setIsFetching(true);
    try {
      const res = await axios.get("http://localhost:8080", {
        withCredentials: true,
      });
      // console.log(res.data.releases);
      setResult(res.data.releases);
    } catch (err) {
      console.error("Backend error:", err);
    } finally {
      setIsFetching(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);
  // useEffect(() => {
  //   console.log(result);
  // }, [result]);

  return (
    <>
      <Navbar></Navbar>
      <div className="grid grid-cols-4 gap-4 mt-20">
        {result && result.map((item) => <Card key={item.id} vinyl={item} />)}
      </div>
    </>
  );
}

export default App;
