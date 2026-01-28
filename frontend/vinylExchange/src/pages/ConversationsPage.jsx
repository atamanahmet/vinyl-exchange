import axios from "axios";
import { useEffect, useState } from "react";
import { button } from "@material-tailwind/react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";
import { useMessagingStore } from "../stores/messagingStore";

export default function ConversationsPage() {
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const isLoading = useAuthStore((state) => state.isLoading);
  const setOpenLogin = useAuthStore((state) => state.setOpenLogin);
  const activeConvoId = useMessagingStore((state) => state.activeConvoId);

  const [conversations, setConversations] = useState();
  const [activeConversation, setActiveConversation] = useState();
  const [fetchConvoId, setFetchConvoId] = useState();
  const [relatedListingId, setRelatedListingId] = useState();
  const [response, setResponse] = useState();
  const { listingId } = useParams();
  const [messages, setMessages] = useState();
  const [newMessage, setNewMessage] = useState("");
  const [participantUsername, setParticipantUsername] = useState();

  useEffect(() => {
    console.log("active convoıd : " + activeConvoId);
    if (activeConvoId != null) {
      fetchMessages(activeConvoId);
    }
  }, [user]);

  //user check
  useEffect(() => {
    if (user == null && !isLoading) {
      setOpenLogin;
      // navigate("/");
    }
  }, []);

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
        { withCredentials: true },
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
        },
      );
      if (res.status == 200) {
        setConversations(res.data);
        console.log(res.data);
      }
    } catch (error) {
      console.log(error);
    }
  }

  //TODO: remove, helper not prod
  async function deleteAllConversations() {
    try {
      const res = await axios.delete(
        `http://localhost:8080/api/messages/conversations`,
        {
          withCredentials: true,
        },
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
        },
      );
      if (res.status == 200) {
        setActiveConversation(res.data.conversationDTO);
        setMessages(res.data.messagePage.content);
        setParticipantUsername(
          user.username == res.data.conversationDTO.initiatorUsername
            ? res.data.conversationDTO.participantUsername
            : res.data.conversationDTO.initiatorUsername,
        );
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
    <div className="flex h-[calc(100vh-64px)] w-full  mx-auto bg-black">
      {/* Sidebar */}
      <div className=" w-2/8  bg-neutral-primary border-r border-neutral-secondary flex flex-col">
        {/* Sidebar Header */}
        <header className="py-4 px-4 border-b border-neutral-secondary bg-accent-primary shrink-0">
          <div className="flex justify-between items-center">
            <h2 className="text-2xl font-semibold text-white">Conversations</h2>
            <button
              className="bg-red-500 hover:bg-red-600 text-white rounded p-0.5 text-sm font-medium transition-colors"
              onClick={() => deleteAllConversations()}
            >
              Delete All
            </button>
          </div>
        </header>

        {/* Contact List */}
        <div className="flex-1 overflow-y-auto">
          {conversations && conversations.length > 0 ? (
            conversations.map((convo) => (
              <button
                key={convo.id}
                onClick={() => fetchMessages(convo.id)}
                className="w-full"
              >
                <div className="px-3 py-2 border-b border-neutral-secondary hover:bg-neutral-secondary-soft transition-colors">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 bg-neutral-secondary-medium rounded-full shrink-0 overflow-hidden">
                      <img
                        src="https://placehold.co/200x/ffa8e4/ffffff.svg?text=ʕ•́ᴥ•̀ʔ&font=Lato"
                        alt="User Avatar"
                        className="w-full h-full object-cover"
                      />
                    </div>
                    <div className="flex-1 text-left min-w-0">
                      <h2 className="text-base font-semibold text-heading truncate">
                        {user && convo.participantUsername == user.username
                          ? convo.initiatorUsername
                          : convo.participantUsername}
                      </h2>
                      <p className="text-sm text-body truncate">
                        {convo.lastMessagePreview}
                      </p>
                    </div>
                  </div>
                </div>
              </button>
            ))
          ) : (
            <div className="flex items-center justify-center h-full">
              <p className="text-body">No conversations yet</p>
            </div>
          )}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col sm:w-4/5 lg:w-4/5 bg-neutral-primary">
        {user && messages ? (
          <>
            {/* Chat Header */}
            <header className="bg-neutral-primary border-b border-neutral-secondary p-3 flex items-center gap-3 shrink-0">
              <img
                src="./placeholder.png"
                alt=""
                className="h-10 w-10 rounded-full bg-neutral-secondary-medium object-cover"
              />
              <h2 className="text-lg font-semibold text-heading">
                {participantUsername}
              </h2>
            </header>

            {/* Messages Container */}
            <div className="flex-1 overflow-y-auto px-4 py-4 space-y-3">
              {messages.map((message) => (
                <div
                  key={message.id}
                  className={`flex ${
                    message.senderUsername === user.username
                      ? "justify-end "
                      : "justify-start"
                  }`}
                >
                  <div
                    className={`max-w-md ${
                      message.senderUsername === user.username
                        ? "bg-amber-600 text-white"
                        : "bg-indigo-950 text-white "
                    } rounded-2xl px-4 py-2.5 shadow-sm`}
                  >
                    {message.senderUsername !== user.username && (
                      <p className="text-xs text-left font-semibold mb-1 text-body">
                        {message.senderUsername}
                      </p>
                    )}
                    <p className="text-sm leading-relaxed wrap-break-words">
                      {message.content}
                    </p>
                    <p
                      className={`text-xs mt-1 ${
                        message.senderUsername === user.username
                          ? "text-white/80 text-right"
                          : "text-body text-left"
                      }`}
                    >
                      {new Date(message.timestamp).toLocaleTimeString("tr-TR", {
                        hour: "numeric",
                        minute: "2-digit",
                      })}
                    </p>
                  </div>
                </div>
              ))}
            </div>

            {/* Chat Input */}
            <footer className="bg-neutral-primary border-t border-neutral-secondary p-4 shrink-0">
              <div className="flex items-end gap-2">
                <textarea
                  placeholder="Type a message..."
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  onKeyDown={handleKeyPress}
                  rows="1"
                  className="flex-1 p-2.5 rounded-base border border-neutral-tertiary text-heading bg-neutral-primary focus:outline-none focus:ring-2 focus:ring-accent-primary focus:border-accent-primary placeholder-body resize-none min-h-[42px] max-h-32"
                  style={{ overflowY: "auto" }}
                />
                <button
                  className="bg-accent-primary hover:bg-accent-primary-dark text-white px-4 py-2.5 rounded-base font-medium transition-colors shrink-0"
                  onClick={handleSend}
                >
                  Send
                </button>
              </div>
            </footer>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center">
            <p className="text-body text-lg">
              Select a conversation to start messaging
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
