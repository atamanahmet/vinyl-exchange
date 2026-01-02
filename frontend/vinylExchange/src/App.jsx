import { useState, useEffect } from "react";
import "./App.css";
import { ThemeProvider } from "@material-tailwind/react";

import Card from "./comps/Card";
import axios from "axios";
import Navbar from "./comps/Navbar";
import MainPage from "./pages/MainPage";
import About from "./pages/About";
import TermsAndConditions from "./pages/TermsAndConditions";
import NewListing from "./pages/NewListing";
import ListingsPage from "./pages/ListingsPage";
import EditListing from "./pages/EditListing";
import ItemPage from "./pages/ItemPage";
import CartPage from "./pages/CartPage";
import OrdersPage from "./pages/OrdersPage";

import { useUser } from "./context/UserContext";
import {
  Routes,
  Route,
  useNavigate,
  useLocation,
  useParams,
} from "react-router-dom";
import AdminDashboard from "./pages/AdminDashboard";
import ErrorPage from "./pages/ErrorPage";
import OrderItemsPage from "./pages/OrderItemsPage";

function App() {
  const { user, data } = useUser();

  const [count, setCount] = useState(0);
  const [result, setResult] = useState();
  const [isFetching, setIsFetching] = useState();

  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<MainPage data={data} />} />
        <Route path="/about" element={<About />} />
        <Route path="/newlisting" element={<NewListing />} />
        <Route path="/listings" element={<ListingsPage />} />
        <Route path="/terms" element={<TermsAndConditions />} />
        <Route path="/edit" element={<EditListing />} />
        <Route path="/listing/:listingId" element={<ItemPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/error" element={<ErrorPage />} />
        <Route path="/orderItems" element={<OrderItemsPage />} />
      </Routes>
    </>
  );
}

export default App;
