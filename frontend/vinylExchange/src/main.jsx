// import React, { StrictMode } from "react";
import "./index.css";

import { BrowserRouter, Routes, Route } from "react-router";
import { createRoot } from "react-dom/client";

import { CartProvider } from "./context/CartContext.jsx";

import App from "./App.jsx";

import { ThemeProvider } from "@material-tailwind/react";
import { UserProvider } from "./context/UserContext.jsx";

createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <UserProvider>
      <CartProvider>
        <App />
      </CartProvider>
    </UserProvider>
  </BrowserRouter>
);
