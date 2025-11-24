import { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router";
import axios from "axios";
const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [isFetching, setIsFetching] = useState(false);
  const [data, setData] = useState();
  const [logoutResult, setLogoutResult] = useState(null);

  const storedPhoto = sessionStorage.getItem("profilePhoto");

  // const fetchUser = () => {
  //   setLoading(true);
  //   axios
  //     .get("http://localhost:8080/api/me", { withCredentials: true })
  //     .then((res) => {
  //       login(res.data);
  //       getProfilePhoto();
  //       getWatchList();
  //     })
  //     .catch((err) => {
  //       console.log("Error: " + err);
  //       setUser(null);
  //     })
  //     .finally(() => {
  //       setLoading(false);
  //     });
  // };

  // useEffect(() => {
  //   fetchUser();
  // }, [location.pathname]);

  async function searchHandler(searchQuery) {
    if (searchQuery != null) {
      searchQuery = searchQuery.replace(" ", "+");

      try {
        const res = await axios.get(
          "http://localhost:8080/search/" + searchQuery,
          {
            withCredentials: true,
          }
        );

        if (res.status === 200) {
          setData(res.data);
          console.log(res.data);
        } else {
          console.error("Search failed with status:", res.status);
        }
      } catch (err) {
        console.log("Search error:", err);
      }
    }
  }
  const fetchData = async () => {
    if (isFetching) return;
    setIsFetching(true);
    try {
      const res = await axios.get("http://localhost:8080", {
        withCredentials: true,
      });
      console.log(res.data);
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
    // const url = "http://localhost:8080/logout";
    // try {
    //   await axios
    //     .post(
    //       url,
    //       {},
    //       {
    //         withCredentials: true,
    //       }
    //     )
    //     .then((res) => setLogoutResult(res.data));

    //   console.log("Logged out");
    //   sessionStorage.clear();
    //   setUser(null);
    // } catch (err) {
    //   console.log("Error during logout: ", err);
    // }
  };

  return (
    <UserContext.Provider
      value={{
        user,
        // updateUser,
        loading,
        setSearchQuery,
        data,
        logOut,
        searchHandler,
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
