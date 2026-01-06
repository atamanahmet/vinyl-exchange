import React, { useState } from "react";

const MessagingPage = () => {
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: "other",
      text: "Hey! I'm interested in your Pink Floyd - Dark Side of the Moon. What condition is it in?",
      timestamp: "10:32 AM",
      senderName: "Alex",
    },
    {
      id: 2,
      sender: "me",
      text: "Hi Alex! It's in excellent condition - VG+/VG+ for both vinyl and sleeve. Original 1973 pressing.",
      timestamp: "10:35 AM",
      senderName: "You",
    },
    {
      id: 3,
      sender: "other",
      text: "Nice! What are you asking for it?",
      timestamp: "10:36 AM",
      senderName: "Alex",
    },
    {
      id: 4,
      sender: "me",
      text: "Looking for $85, but I'm open to trades. What do you have?",
      timestamp: "10:38 AM",
      senderName: "You",
    },
    {
      id: 5,
      sender: "other",
      text: "I have a few things... Led Zeppelin II, Fleetwood Mac - Rumours, and some Beatles records. Any interest?",
      timestamp: "10:42 AM",
      senderName: "Alex",
    },
  ]);

  const [newMessage, setNewMessage] = useState("");

  const handleSend = () => {
    if (newMessage.trim()) {
      const message = {
        id: messages.length + 1,
        sender: "me",
        text: newMessage,
        timestamp: new Date().toLocaleTimeString("en-US", {
          hour: "2-digit",
          minute: "2-digit",
        }),
        senderName: "You",
      };
      setMessages([...messages, message]);
      setNewMessage("");
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="flex flex-col h-screen w-screen max-w-[900px] bg-black">
      {/* Header */}
      <div className="bg-white border-b px-6 py-4 shadow-sm">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-pink-500 rounded-full flex items-center justify-center text-white font-semibold">
            A
          </div>
          <div>
            <h2 className="font-semibold text-gray-900">Alex</h2>
            <p className="text-sm text-gray-500">Active now</p>
          </div>
        </div>
        <div></div>
      </div>

      {/* Messages Area */}
      <div className="flex-1 overflow-y-auto px-6 py-4 space-y-4">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`flex ${
              message.sender === "me" ? "justify-end" : "justify-start"
            }`}
          >
            <div
              className={`max-w-md ${
                message.sender === "me"
                  ? "bg-blue-500 text-white"
                  : "bg-white text-gray-900 border border-gray-200"
              } rounded-2xl px-4 py-2.5 shadow-sm`}
            >
              <p className="text-sm leading-relaxed">{message.text}</p>
              <p
                className={`text-xs mt-1 ${
                  message.sender === "me" ? "text-blue-100" : "text-gray-500"
                }`}
              >
                {message.timestamp}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* Input Area */}
      <div className="bg-white border-t px-6 py-4">
        <div className="flex gap-3 items-end">
          <div className="flex-1">
            <textarea
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              onKeyDown={handleKeyPress}
              placeholder="Type your message..."
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
              rows="1"
            />
          </div>
          <button
            onClick={handleSend}
            className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-xl font-medium transition-colors"
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );
};

export default MessagingPage;
