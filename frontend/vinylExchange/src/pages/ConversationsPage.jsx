import axios from "axios";
import { useEffect, useState } from "react";
import { useUser } from "../context/UserContext";
import { button } from "@material-tailwind/react";

export default function ConversationsPage() {
  const [conversations, setConversations] = useState();

  const [messages, setMessages] = useState();
  const { user } = useUser();

  async function fetchConversations() {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/messages/conversations`,
        {
          withCredentials: true,
        }
      );
      if (res.status == 200) {
        setConversations(res.data);
        console.log(res.data);
      }
    } catch (error) {
      console.log(error);
    }
  }
  async function fetchMessages(conversationId) {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/messages/conversation/${conversationId}`,
        {
          withCredentials: true,
        }
      );
      if (res.status == 200) {
        setMessages(res.data);
        console.log(res.data);
      }
    } catch (error) {
      console.log(error);
    }
  }
  async function fetchConversations() {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/messages/conversations`,
        {
          withCredentials: true,
        }
      );
      if (res.status == 200) {
        setConversations(res.data);
        console.log(res.data);
      }
    } catch (error) {
      console.log(error);
    }
  }

  useEffect(() => {
    fetchConversations();
  }, []);

  return (
    <>
      <div className="flex h-screen min-w-300 overflow-hidden">
        {/*  Sidebar  */}
        <div className="min-w-1/4 bg-black border-r border-gray-300">
          {/*  Sidebar Header  */}
          <header className="p-4 border-b flex justify-between items-center bg-indigo-600 text-white">
            <h1 className="text-md font-semibold">Convos</h1>
            <div className="relative">
              {/* <button id="menuButton" className="focus:outline-none">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5 text-gray-100"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                  <path d="M2 10a2 2 0 012-2h12a2 2 0 012 2 2 2 0 01-2 2H4a2 2 0 01-2-2z" />
                </svg>
              </button> */}
              {/*  Menu Dropdown  */}
              <div
                id="menuDropdown"
                className="absolute right-0 mt-2 w-48 bg-white border border-gray-300 rounded-md shadow-lg hidden"
              ></div>
            </div>
          </header>

          {/*  Contact List  */}
          <div className="flex flex-col flex-wrap">
            {conversations &&
              conversations.map((convo) => (
                <button onClick={() => fetchMessages(convo.relatedListingId)}>
                  <div className="px-2 pt-1.5 pb-1.5">
                    <div className="flex items-center cursor-pointer text-left hover:bg-gray-100 hover:text-black border border-white p-2 rounded-md">
                      <div className="w-12 h-12 bg-gray-300   rounded-full mr-3">
                        <img
                          src="https://placehold.co/200x/ffa8e4/ffffff.svg?text=ʕ•́ᴥ•̀ʔ&font=Lato"
                          alt="User Avatar"
                          className="w-12 h-12 rounded-full"
                        />
                      </div>
                      <div className="flex-1">
                        <h2 className="text-lg font-semibold">
                          {convo.participantUsername == user.username
                            ? convo.initiatorUsername
                            : convo.participantUsername}{" "}
                        </h2>
                        <p className="text-gray-600">
                          {convo.lastMessagePreview}{" "}
                        </p>
                      </div>
                    </div>
                  </div>
                </button>
              ))}
          </div>
        </div>

        {/*  Main Chat Area  */}
        <div className="flex-1">
          {messages && (
            <>
              {/*  Chat Header  */}
              <header className="bg-black p-4 text-white">
                <h1 className="text-2xl font-semibold">{}</h1>
              </header>

              {/*  Chat Messages  */}
              <div className="h-screen overflow-y-auto p-4 pb-36">
                {/*  Incoming Message  */}
                <div className="flex mb-4 cursor-pointer">
                  <div className="w-9 h-9 rounded-full flex items-center justify-center mr-2">
                    <img
                      src="https://placehold.co/200x/ffa8e4/ffffff.svg?text=ʕ•́ᴥ•̀ʔ&font=Lato"
                      alt="User Avatar"
                      className="w-8 h-8 rounded-full"
                    />
                  </div>
                  <div className="flex max-w-96 bg-white rounded-lg p-3 gap-3">
                    <p className="text-gray-700">Hey Bob, how's it going?</p>
                  </div>
                </div>

                {/*  Outgoing Message  */}
                <div className="flex justify-end mb-4 cursor-pointer">
                  <div className="flex max-w-96 bg-indigo-500 text-white rounded-lg p-3 gap-3">
                    <p>
                      Hi Alice! I'm good, just finished a great book. How about
                      you?
                    </p>
                  </div>
                  <div className="w-9 h-9 rounded-full flex items-center justify-center ml-2">
                    <img
                      src="https://placehold.co/200x/b7a8ff/ffffff.svg?text=ʕ•́ᴥ•̀ʔ&font=Lato"
                      alt="My Avatar"
                      className="w-8 h-8 rounded-full"
                    />
                  </div>
                </div>
              </div>
            </>
          )}

          {/*  Chat Input  */}
          <footer className="bg-white border-t border-gray-300 p-4 absolute bottom-0 w-3/4">
            <div className="flex items-center">
              <input
                type="text"
                placeholder="Type a message..."
                className="w-full p-2 rounded-md border border-gray-400 focus:outline-none focus:border-blue-500"
              />
              <button className="bg-indigo-500 text-white px-4 py-2 rounded-md ml-2">
                Send
              </button>
            </div>
          </footer>
        </div>
      </div>
    </>
  );
}
