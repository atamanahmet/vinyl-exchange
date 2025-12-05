import { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [data, setData] = useState();
  const [authResponse, setAuthResponse] = useState(null);

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
        { withCredentials: true }
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
      setResponse("Got some issue. Try again");
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
        { withCredentials: true }
      );

      if (response.status === 200) {
        setAuthResponse("Logged in succesfully. Redirecting...");

        const { username, email } = response.data;

        setTimeout(function () {
          setUser({ username, email });
        }, 2000);
      }
    } catch (error) {
      console.log(error);
      setAuthResponse("Got some issue. Try again");
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

  async function currentUser() {
    const url = "http://localhost:8080/api/me";
    try {
      const res = await axios.get(url, {
        withCredentials: true,
      });

      if (res.status === 200) {
        setUser(res.data);
      } else {
        console.log("No current user present");
      }
    } catch (error) {
      console.log("Issue while getting user: ", error);
    }
  }

  useEffect(() => {
    currentUser();
  }, [location.pathname]);

  const fetchData = async () => {
    if (isFetching) return;
    setIsFetching(true);
    const url = "http://localhost:8080";

    try {
      const res = await axios.get(url, {
        withCredentials: true,
      });

      setData(res.data);
    } catch (err) {
      console.error("Backend error:", err);
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
        sessionStorage.clear();
        setUser(null);
      }
    } catch (err) {
      console.log("Error during logout: ", err);
    }
  };

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
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

// Custom hook for easy access
export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUser must be used within a UserProvider");
  }
  return context;
};
