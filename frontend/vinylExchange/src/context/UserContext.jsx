import { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useEffectEvent } from "react";

const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [navbarActive, setNavbarActive] = useState(true);

  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);
  const [loggedIn, setLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [openLogin, setOpenLogin] = useState(false);
  const [data, setData] = useState();
  const [authResponse, setAuthResponse] = useState(null);
  const [activeConvoId, setActiveConvoId] = useState();
  const [hasError, setHasError] = useState(false);

  const [layoutSelection, setLayoutSelection] = useState(() => {
    return localStorage.getItem("layoutSelection") || "grid";
  });
  useEffect(() => {
    localStorage.setItem("layoutSelection", layoutSelection);
  }, [layoutSelection]);

  const storedPhoto = sessionStorage.getItem("profilePhoto");

  // register
  async function registerUser(formData) {
    const url = "http://localhost:8080/register";

    try {
      const response = await axios.post(
        url,
        {
          username: formData.username,
          password: formData.password,
          email: formData.email,
        },
        { withCredentials: true },
      );
      if (response.status === 201) {
        setAuthResponse("Account created succesfully. Redirecting...");
        const { username, email } = response.data;
        setTimeout(function () {
          setUser({ username, email });
        }, 2000);
      }
    } catch (error) {
      console.log(error);
      setAuthResponse("Register issues, try again or change credentials");
    }
  }

  // login
  async function loginUser(formData) {
    const url = "http://localhost:8080/login";

    try {
      const response = await axios.post(
        url,
        {
          username: formData.username,
          password: formData.password,
        },
        { withCredentials: true },
      );

      if (response.status === 200) {
        setAuthResponse("Logged in succesfully. Redirecting...");
        setLoggedIn(true);
        const { username, email } = response.data;
        setTimeout(function () {
          setUser({ username, email });
        }, 2000);
      }
    } catch (error) {
      console.log(error);
      setAuthResponse("Wrong credentials. Try again or register");
    }
  }

  async function searchHandler(searchQuery) {
    if (searchQuery != null) {
      searchQuery = searchQuery.replace(" ", "+");

      const url = "http://localhost:8080/search/";

      try {
        const res = await axios.get(url + searchQuery, {
          withCredentials: true,
        });

        if (res.status === 200) {
          setData(res.data);
        } else {
          console.error("Search failed with status:", res.status);
        }
      } catch (err) {
        console.log("Search error:", err);
      }
    }
  }

  async function checkAuth() {
    setLoading(true);

    try {
      const res = await axios.get("http://localhost:8080/api/me", {
        withCredentials: true,
      });
      setUser(res.data);
    } catch (error) {
      setUser(null); // Not logged in, that's fine
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    checkAuth();
  }, []);

  const fetchData = async () => {
    if (location.pathname === "/error") {
      setNavbarActive(false);
      setIsFetching(false);
      return;
    }
    if (isFetching) return;
    setIsFetching(true);
    const url = "http://localhost:8080/api/listings";

    try {
      const res = await axios.get(url, {
        withCredentials: true,
      });

      setData(res.data);
      setNavbarActive(true);
      setHasError(false);
    } catch (err) {
      console.error("Backend error:", err);
      setNavbarActive(false);
      setHasError(true);
    } finally {
      setIsFetching(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [location.pathname]);

  const logOut = async () => {
    console.log("Log out requested");
    const url = "http://localhost:8080/logout";
    try {
      const res = await axios.post(url, null, {
        withCredentials: true,
      });
      if (res.status === 204) {
        console.log("Logged out");
        setLoggedIn(false);

        sessionStorage.clear();
        setUser(null);
      }
    } catch (err) {
      console.log("Error during logout: ", err);
    }
  };

  async function startConversation(relatedListingId) {
    try {
      const res = await axios.post(
        `http://localhost:8080/api/messages/start`,
        { relatedListingId: relatedListingId },
        { withCredentials: true },
      );
      if (res.status == 201) {
        console.log("convo started");
        setActiveConvoId(res.data.id);
      }
    } catch (error) {
      console.log("convo starting error " + error);
    } finally {
      navigate(`/messaging`);
    }
  }

  return (
    <UserContext.Provider
      value={{
        user,
        loading,
        setSearchQuery,
        data,
        logOut,
        searchHandler,
        registerUser,
        loginUser,
        authResponse,
        openLogin,
        navbarActive,
        currentUserId,
        setLayoutSelection,
        layoutSelection,
        startConversation,
        activeConvoId,
        isFetching,
        hasError,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("UserProvider??");
  }
  return context;
};
