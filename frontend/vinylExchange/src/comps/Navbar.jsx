import { useEffect, useState } from "react";
import { useNavigate } from "react-router";

import { AuthModal } from "./AuthModal";

import { useCartStore } from "../stores/cartStore";
import { useAuthStore } from "../stores/authStore";
import { useUIStore } from "../stores/uiStore";
import { useDataStore } from "../stores/dataStore";

const SkeletonNavbar = () => (
  <nav className="bg-black fixed w-full z-20 top-0 start-0 p-2 border-b border-default">
    <div className="max-w-6xl flex max-h-14 items-center justify-between mx-auto p-1 flex-wrap">
      {/* logo */}
      <div className="flex items-center space-x-3 rtl:space-x-reverse">
        <div className="h-12 w-12 bg-neutral-700 rounded-full animate-pulse"></div>
        <div className="h-6 w-28 bg-neutral-700 rounded animate-pulse"></div>
      </div>

      <div className="flex items-center md:order-2 gap-2">
        {/* mobile search button */}
        <div className="flex items-center justify-center md:hidden bg-neutral-700 rounded-base w-10 h-10 animate-pulse max-[499px]:hidden"></div>

        {/* sign in bvutton */}
        <div className="h-9 w-24 bg-neutral-700 rounded-lg animate-pulse max-[499px]:hidden"></div>

        {/* cart icon */}
        <div className="h-8 w-8 bg-neutral-700 rounded animate-pulse"></div>

        {/* message icon */}
        <div className="h-8 w-8 bg-neutral-700 rounded animate-pulse"></div>

        {/* avatar */}
        <div className="h-8 w-8 bg-neutral-700 rounded-full animate-pulse"></div>

        {/* mobile menu*/}
        <div className="min-[850px]:hidden w-10 h-10 bg-neutral-700 rounded-base animate-pulse"></div>
      </div>

      {/* navigation */}
      <div className="hidden min-[850px]:flex min-[850px]:w-auto min-[850px]:order-1">
        <ul className="font-medium flex flex-row space-x-8">
          <li>
            <div className="h-4 w-12 bg-neutral-700 rounded animate-pulse"></div>
          </li>
          <li>
            <div className="h-4 w-14 bg-neutral-700 rounded animate-pulse"></div>
          </li>
          <li>
            <div className="h-4 w-16 bg-neutral-700 rounded animate-pulse"></div>
          </li>
          <li>
            <div className="h-4 w-14 bg-neutral-700 rounded animate-pulse"></div>
          </li>
          <li>
            <div className="h-4 w-20 bg-neutral-700 rounded animate-pulse"></div>
          </li>
        </ul>
      </div>
    </div>
  </nav>
);

export default function Navbar() {
  const navigate = useNavigate();

  const cartItemCount = useCartStore((state) => state.cartItemCount);
  const user = useAuthStore((state) => state.user);
  const logOut = useAuthStore((state) => state.logOut);
  const isLoading = useAuthStore((state) => state.isLoading);
  const setSearchQuery = useDataStore((state) => state.setSearchQuery);
  const search = useDataStore((state) => state.search);
  const openLogin = useUIStore((state) => state.openLogin);
  const navbarActive = useUIStore((state) => state.navbarActive);
  const setOpenLogin = useUIStore((state) => state.setOpenLogin);

  const [query, setQuery] = useState("");
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const [isMobileSearchOpen, setIsMobileSearchOpen] = useState(false);

  const handleSearch = (e) => {
    if (e) e.preventDefault();
    navigate("/");
    searchHandler(query);
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleSearch(e);
    }
  };
  const handleLogOut = () => logOut;

  const setModalActive = () => {
    setOpenLogin(!openLogin);
  };

  if (isLoading) {
    return <SkeletonNavbar />;
  }

  if (!navbarActive) {
    return null;
  }

  return (
    <nav className="bg-black fixed w-full z-20 top-0 start-0 p-2 border-b border-default">
      <div className="max-w-6xl flex max-h-14 items-center justify-between mx-auto p-1 flex-wrap">
        {/* Logo */}
        <a href="/" className="flex items-center space-x-3 rtl:space-x-reverse">
          <img
            src="/logo.png"
            className="h-12 bg-red-500 rounded-full"
            alt="VexChange Logo"
          />
          <span className="self-center text-xl text-heading font-semibold whitespace-nowrap">
            VexChange
          </span>
        </a>

        <div className="flex items-center md:order-2 gap-2">
          <button
            type="button"
            onClick={() => setIsMobileSearchOpen(!isMobileSearchOpen)}
            className="flex items-center justify-center md:hidden text-body hover:text-heading bg-transparent box-border border border-transparent hover:bg-neutral-secondary-medium focus:ring-2 focus:ring-neutral-tertiary font-medium leading-5 rounded-base text-sm w-10 h-10 focus:outline-none max-[499px]:hidden"
          >
            <svg
              className="w-6 h-6"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <path
                stroke="currentColor"
                strokeLinecap="round"
                strokeWidth="2"
                d="m21 21-3.5-3.5M17 10a7 7 0 1 1-14 0 7 7 0 0 1 14 0Z"
              />
            </svg>
            <span className="sr-only">Search</span>
          </button>

          {/* Desktop Search */}
          <div className="relative hidden md:hidden">
            <div className="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none ">
              <svg
                className="w-4 h-4 text-body "
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  stroke="currentColor"
                  strokeLinecap="round"
                  strokeWidth="2"
                  d="m21 21-3.5-3.5M17 10a7 7 0 1 1-14 0 7 7 0 0 1 14 0Z"
                />
              </svg>
            </div>
            <input
              type="text"
              className="block w-full ps-9 pe-3 py-2.5 bg-neutral-secondary-medium border rounded-md border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand shadow-xs placeholder:text-body "
              placeholder="Search"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={handleKeyPress}
            />
          </div>

          {user == null && (
            <div>
              <button
                className="rounded-lg py-2 px-3 text-amber-50 bg-indigo-600 hover:bg-indigo-800 hover:text-slate-200"
                onClick={setModalActive}
              >
                Sign in
              </button>
              <AuthModal
                openModal={openLogin}
                setOpenModal={setModalActive}
              ></AuthModal>
            </div>
          )}

          {user != null && (
            <div>
              <button
                className="rounded-lg py-2 px-3 text-amber-50 bg-indigo-600 hover:bg-indigo-800 hover:text-slate-200 max-[499px]:hidden"
                onClick={() => navigate("/newlisting")}
              >
                New Listing
              </button>
            </div>
          )}

          {/* User Menu */}
          {user != null && (
            <div className="relative">
              <div className="flex gap-2">
                <div className="flex flex-row gap-3">
                  <div className="relative">
                    <a href="/cart">
                      <svg
                        fill="#615fff"
                        version="1.1"
                        id="cart"
                        width="32px"
                        height="35px"
                        viewBox="0 0 453.73 453.73"
                      >
                        <g>
                          <path
                            d="M447.664,129.262c-5.005-6.031-12.435-9.521-20.271-9.521h-20.86l4.734-4.733c1.641-1.642,2.562-3.867,2.562-6.188
		c0-2.321-0.922-4.547-2.562-6.188l-48.674-48.673c-3.415-3.417-8.956-3.416-12.375,0.001l-56.886,56.887v-50.7
		c0-4.832-3.918-8.75-8.75-8.75H174.265c-4.832,0-8.75,3.918-8.75,8.75v59.511c0,0.028,0.004,0.056,0.004,0.083h-34.664
		l-2.876-14.948c-1.838-9.543-8.78-17.301-18.063-20.18L34.149,61.111C20.257,56.802,5.5,64.571,1.189,78.465
		c-4.31,13.894,3.461,28.65,17.354,32.96l60.689,18.824l46.254,202.948c1.612,8.584,7.281,15.535,14.797,19.027
		c-0.223,1.806-0.352,3.639-0.352,5.501c0,24.599,20.013,44.609,44.61,44.609c24.597,0,44.61-20.012,44.61-44.609
		c0-1.026-0.047-2.042-0.117-3.052h70.424c-0.067,1.01-0.115,2.024-0.115,3.052c0,24.599,20.012,44.609,44.608,44.609
		c24.599,0,44.609-20.012,44.609-44.609c0-1.101-0.054-2.187-0.132-3.267c11.271-1.366,20.564-9.866,22.704-21.263l42.145-182.255
		C454.726,143.239,452.667,135.293,447.664,129.262z M184.543,379.451c-11.979,0-21.727-9.746-21.727-21.727
		c0-11.979,9.748-21.727,21.727-21.727c11.979,0,21.727,9.747,21.727,21.727C206.27,369.705,196.522,379.451,184.543,379.451z
		 M343.953,379.451c-11.979,0-21.725-9.746-21.725-21.727c0-11.979,9.745-21.727,21.725-21.727c11.98,0,21.727,9.747,21.727,21.727
		C365.68,369.705,355.934,379.451,343.953,379.451z M359.263,289.074H178.754c-8.729,0-15.804-7.076-15.804-15.805
		c0-8.728,7.075-15.803,15.804-15.803h180.509c8.729,0,15.804,7.075,15.804,15.803C375.067,281.998,367.991,289.074,359.263,289.074
		z M359.263,204.79H178.754c-8.729,0-15.804-7.076-15.804-15.804s7.075-15.803,15.804-15.803h180.509
		c8.729,0,15.804,7.075,15.804,15.803S367.991,204.79,359.263,204.79z"
                          />
                        </g>
                      </svg>
                    </a>
                    <p
                      className={
                        cartItemCount == 0
                          ? "hidden"
                          : "text-indigo-900 absolute -top-2 -right-2 bg-white rounded-full w-6"
                      }
                    >
                      {cartItemCount}
                    </p>
                  </div>
                  <a href="/messaging">
                    <svg
                      width="30px"
                      height="35px"
                      viewBox="0 0 1024 1024"
                      className="icon"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M896 192H128c-35.3 0-64 28.7-64 64v512c0 35.3 28.7 64 64 64h576.6l191.6 127.7L896 832c35.3 0 64-28.7 64-64V256c0-35.3-28.7-64-64-64z"
                        fill="#3D5AFE"
                      />
                      <path
                        d="M640 512c0-125.4-51.5-238.7-134.5-320H128c-35.3 0-64 28.7-64 64v512c0 35.3 28.7 64 64 64h377.5c83-81.3 134.5-194.6 134.5-320z"
                        fill="#536DFE"
                      />
                      <path
                        d="M256 512m-64 0a64 64 0 1 0 128 0 64 64 0 1 0-128 0Z"
                        fill="#FFFFFF"
                      />
                      <path
                        d="M512 512m-64 0a64 64 0 1 0 128 0 64 64 0 1 0-128 0Z"
                        fill="#FFFFFF"
                      />
                      <path
                        d="M768 512m-64 0a64 64 0 1 0 128 0 64 64 0 1 0-128 0Z"
                        fill="#FFFFFF"
                      />
                    </svg>
                  </a>
                </div>
                <button
                  type="button"
                  onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
                  className="flex text-sm bg-neutral-primary rounded-full focus:ring-4 focus:ring-neutral-tertiary"
                >
                  <span className="sr-only">Open user menu</span>
                  <img
                    className="w-8 h-8 rounded-full bg-white"
                    src="/logo.png"
                    alt="user photo"
                  />
                </button>
              </div>
              {/* Invisible backdrop */}
              {isUserMenuOpen && (
                <div
                  className="fixed inset-0 z-40"
                  onClick={() => setIsUserMenuOpen(false)}
                />
              )}

              {/* User Dropdown */}
              {isUserMenuOpen && (
                <div className="absolute bg-black right-0 mt-2 z-50 bg-neutral-primary-medium border border-default-medium rounded-xl shadow-lg w-44 ">
                  <div className="px-4 py-3 text-sm border-b border-default">
                    <span className="block text-heading font-medium">
                      {user?.username || ""}
                    </span>
                    <span className="block text-body truncate">
                      {user.email}
                    </span>
                  </div>
                  <ul className="p-2 text-sm text-body font-medium">
                    <li>
                      <a
                        href="/listings"
                        className="inline-flex items-center w-full p-2 hover:bg-neutral-tertiary-medium hover:text-heading rounded"
                      >
                        My listings
                      </a>
                    </li>
                    <li>
                      <a
                        href="#"
                        className="inline-flex items-center w-full p-2 hover:bg-neutral-tertiary-medium hover:text-heading rounded"
                      >
                        Profile
                      </a>
                    </li>
                    <li>
                      <a
                        href="#"
                        className="inline-flex items-center w-full p-2 hover:bg-neutral-tertiary-medium hover:text-heading rounded"
                      >
                        Earnings
                      </a>
                    </li>
                    <li>
                      <button
                        className="inline-flex items-center w-full p-2 hover:bg-neutral-tertiary-medium hover:text-heading rounded"
                        onClick={logOut}
                      >
                        Sign out
                      </button>
                    </li>
                  </ul>
                </div>
              )}
            </div>
          )}

          {/* Mobile Menu Toggle */}
          <button
            type="button"
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="inline-flex items-center p-2 w-10 h-10 justify-center text-sm text-body rounded-base min-[850px]:hidden hover:bg-neutral-secondary-soft hover:text-heading focus:outline-none focus:ring-2 focus:ring-neutral-tertiary"
          >
            <span className="sr-only">Open main menu</span>
            <svg
              className="w-6 h-6"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <path
                stroke="currentColor"
                strokeLinecap="round"
                strokeWidth="2"
                d="M5 7h14M5 12h14M5 17h14"
              />
            </svg>
          </button>
        </div>
        {/* Invisible backdrop */}
        {/* {isMobileMenuOpen && (
            <div
              className="fixed inset-0 z-40"
              onClick={() => setIsMobileMenuOpen(false)}
            />
          )} */}
        {/* Navigation Links */}
        <div
          className={`${
            isMobileMenuOpen ? "block" : "hidden"
          } w-full min-[850px]:flex min-[850px]:w-auto min-[850px]:order-1`}
        >
          {/* Mobile Search */}
          {isMobileSearchOpen && (
            <div className="relative mt-3 min-[850px]:hidden">
              <div className="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none">
                <svg
                  className="w-4 h-4 text-body"
                  aria-hidden="true"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke="currentColor"
                    strokeLinecap="round"
                    strokeWidth="2"
                    d="m21 21-3.5-3.5M17 10a7 7 0 1 1-14 0 7 7 0 0 1 14 0Z"
                  />
                </svg>
              </div>
              <input
                type="text"
                className="block w-full ps-9 pe-3 py-2 bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand shadow-xs placeholder:text-body"
                placeholder="Search"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={handleKeyPress}
              />
            </div>
          )}

          <ul className="font-medium flex flex-col p-4 min-[850px]:p-0 mt-4 -ml-35 border border-default rounded-base bg-neutral-secondary-soft min-[850px]:flex-row min-[850px]:space-x-8 rtl:space-x-reverse min-[850px]:mt-0 min-[850px]:border-0 min-[850px]:bg-neutral-primary bg-black ">
            <li>
              <a
                href="/"
                className="block py-2 px-3 text-white bg-brand rounded min-[850px]:bg-transparent min-[850px]:text-fg-brand min-[850px]:p-0 "
                aria-current="page"
              >
                Home
              </a>
            </li>
            <li>
              <a
                href="/about"
                className="block py-2 px-3 text-heading rounded hover:bg-neutral-tertiary min-[850px]:hover:bg-transparent min-[850px]:border-0 min-[850px]:hover:text-fg-brand min-[850px]:p-0"
              >
                About
              </a>
            </li>
            <li>
              <a
                href="#"
                className="block py-2 px-3 text-heading rounded hover:bg-neutral-tertiary min-[850px]:hover:bg-transparent min-[850px]:border-0 min-[850px]:hover:text-fg-brand min-[850px]:p-0"
              >
                Services
              </a>
            </li>
            <li>
              <a
                href="/orders"
                className="block py-2 px-3 text-heading rounded hover:bg-neutral-tertiary min-[850px]:hover:bg-transparent min-[850px]:border-0 min-[850px]:hover:text-fg-brand min-[850px]:p-0"
              >
                Orders
              </a>
            </li>
            <li>
              <a
                href="/admin"
                className="block py-2 px-3 text-heading rounded hover:bg-neutral-tertiary min-[850px]:hover:bg-transparent min-[850px]:border-0 min-[850px]:hover:text-fg-brand min-[850px]:p-0"
              >
                AdminDash
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}
