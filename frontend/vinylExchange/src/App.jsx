import { useState, useEffect } from "react";
import "./App.css";
import { ThemeProvider } from "@material-tailwind/react";
import Card from "./comps/Card";
import axios from "axios";
import Navbar from "./comps/Navbar";
import MainPage from "./pages/MainPage";
import About from "./pages/About";
import TermsAndConditions from "./pages/TermsAndConditions";
import { useUser } from "./context/UserContext";
import {
  Routes,
  Route,
  useNavigate,
  useLocation,
  useParams,
} from "react-router-dom";

function App() {
  const { user, data } = useUser();

  const [count, setCount] = useState(0);
  const [result, setResult] = useState();
  const [isFetching, setIsFetching] = useState();

  return (
    <>
      <Navbar></Navbar>
      <Routes>
        <Route path="/" element={<MainPage data={data} />} />
        <Route path="/about" element={<About />} />
        <Route path="/terms" element={<TermsAndConditions />} />
      </Routes>
    </>
  );
}

export default App;
