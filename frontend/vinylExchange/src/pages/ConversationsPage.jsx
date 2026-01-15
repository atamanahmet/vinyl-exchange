import axios from "axios";
import { useEffect, useState } from "react";
import { useUser } from "../context/UserContext";
import { button } from "@material-tailwind/react";
import { useNavigate, useParams } from "react-router-dom";

export default function ConversationsPage() {
  const navigate = useNavigate();

  const [conversations, setConversations] = useState();

  const [activeConversation, setActiveConversation] = useState();

  const [fetchConvoId, setFetchConvoId] = useState();

  const [relatedListingId, setRelatedListingId] = useState();

  const [response, setResponse] = useState();

  const { listingId } = useParams();

  const [messages, setMessages] = useState();

  const [newMessage, setNewMessage] = useState("");

  const [participantUsername, setParticipantUsername] = useState();

  const { user, loading, activeConvoId } = useUser();

  useEffect(() => {
    console.log("active convoıd : " + activeConvoId);
    if (activeConvoId != null) {
      fetchMessages(activeConvoId);
    }
  }, []);

  //user check
  useEffect(() => {
    if (user == null && !loading) {
      navigate("/");
    }
  }, []);

  useEffect(() => {
    if (user == null && !loading) {
      navigate("/");
    }
  }, [user]);

  const handleSend = async () => {
    if (newMessage == "") {
      return;
    }
    try {
      const res = await axios.post(
        "http://localhost:8080/api/messages",
        {
          conversationId: activeConversation.id,
          relatedListingId: activeConversation.relatedListingId,
          content: newMessage,
        },
        { withCredentials: true }
      );

      console.log(res.data);
      if (res.status == 200) {
        setResponse(res.data);
      }
    } catch (error) {
      console.log(error);
    } finally {
      fetchMessages(activeConversation.id);
    }

    setNewMessage("");
  };

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

  //helper not prod
  async function deleteAllConversations() {
    try {
      const res = await axios.delete(
        `http://localhost:8080/api/messages/conversations`,
        {
          withCredentials: true,
        }
      );
      if (res.status == 204) {
        console.log("deleted");
        setConversations();
        setActiveConversation();
      }
    } catch (error) {
      console.log(error);
    }
  }

  async function fetchMessages(activeConversationId) {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/messages/conversation/${activeConversationId}`,
        {
          withCredentials: true,
        }
      );
      if (res.status == 200) {
        setActiveConversation(res.data.conversationDTO);
        setMessages(res.data.messagePage.content);
        setParticipantUsername(
          user.username == res.data.conversationDTO.initiatorUsername
            ? res.data.conversationDTO.participantUsername
            : res.data.conversationDTO.initiatorUsername
        );
      }
    } catch (error) {
      console.log(error);
    }
  }

  // useEffect(() => {
  //   if (activeConversation != null && user != null) {
  //     fetchMessages(activeConversation.id);
  //   }
  // }, [activeConversation]);

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
        console.log("convos: ", res.data);
      }
    } catch (error) {
      console.log(error);
    }
  }

  const handleKeyPress = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  useEffect(() => {
    fetchConversations();
  }, [user]);

  return (
    <>
      <div className="flex h-240 min-w-300 overflow-hidden -mt-5">
        {/*  Sidebar  */}
        <div className="min-w-1/4 bg-black border-r border-gray-300">
          {/*  Sidebar Header  */}
          <header className="py-2 px-2 border-b  bg-indigo-600 text-white">
            <div className="flex justify-between">
              <h2 className="text-3xl font-semibold">Convos</h2>
              <button
                className=" bg-red-500 rounded-xl p-2 "
                onClick={() => deleteAllConversations()}
              >
                Delete All
              </button>
            </div>
          </header>

          {/*  Contact List  */}
          <div className="flex flex-col flex-wrap">
            {conversations &&
              conversations.map((convo) => (
                <button key={convo.id} onClick={() => fetchMessages(convo.id)}>
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
                          {user && convo.participantUsername == user.username
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
        <div className="flex flex-col w-9/9 border-5  border-x-8 rounded-tr-2xl mt-0.5 border-indigo-900">
          {user && messages && (
            <>
              {/*  Chat Header  */}
              <header className="bg-black rounded-tr-xl border-b-2 border-indigo-900 p-4 text-white w-full flex flex-row gap-4 items-center">
                <img
                  src="./placeholder.png"
                  alt=""
                  className="h-[35px] w-[35px] rounded-full bg-white"
                />
                <h2 className="text-xl text-left font-semibold">
                  {participantUsername}
                </h2>
              </header>

              <div className="flex-1 overflow-y-auto px-6 py-4 space-y-4">
                {user &&
                  messages.map((message) => (
                    <div
                      key={message.id}
                      className={`flex ${
                        message.senderUsername === user.username
                          ? "justify-end"
                          : "justify-start"
                      }`}
                    >
                      <div
                        className={`max-w-md ${
                          message.senderUsername === user.username
                            ? "bg-blue-500 text-white"
                            : "bg-white text-gray-900 border border-gray-200"
                        } rounded-2xl px-4 py-2.5 shadow-sm`}
                      >
                        {message.senderUsername !== user.username && (
                          <p className="text-xs text-left font-semibold mb-1 text-gray-600">
                            {message.senderUsername}
                          </p>
                        )}
                        <p className="text-sm leading-relaxed">
                          {message.content}
                        </p>
                        <p
                          className={`text-xs mt-1 ${
                            message.senderUsername === user.username
                              ? "text-blue-100 text-right"
                              : "text-gray-500 text-left"
                          }`}
                        >
                          {new Date(message.timestamp).toLocaleTimeString(
                            "tr-TR",
                            {
                              hour: "numeric",
                              minute: "2-digit",
                            }
                          )}
                        </p>
                      </div>
                    </div>
                  ))}
              </div>

              {/* Chat Messages 
              <div className="h-screen overflow-y-auto p-4 pb-36">
                {/*  Incoming Message  */}
              {/* <div className="flex mb-4 cursor-pointer">
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
                </div> */}

              {/*  Outgoing Message  */}
              {/* <div className="flex justify-end mb-4 cursor-pointer">
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
              </div> */}
            </>
          )}

          {/*  Chat Input  */}
          <footer className="bg-white border-t border-gray-300 p-4 absolute bottom-0 w-220">
            <div className="flex items-center">
              <textarea
                type="text"
                placeholder="Type a message..."
                onChange={(e) => setNewMessage(e.target.value)}
                onKeyDown={handleKeyPress}
                className="w-full p-2 rounded-md border text-black border-gray-400 focus:outline-none focus:border-blue-500"
              />
              <button
                className="bg-indigo-600 text-white px-4 py-2 rounded-md ml-2"
                onClick={handleSend}
              >
                Send
              </button>
            </div>
          </footer>
        </div>
      </div>
    </>
  );
}
