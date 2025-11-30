"use client";

import {
  Button,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
} from "flowbite-react";
import { useState } from "react";
import { Login } from "./Login";
import { Register } from "./Register";
import { useUser } from "../context/UserContext";

export function AuthModal({ openModal, setOpenModal }) {
  const { authResponse } = useUser();
  const [authType, setAuthType] = useState("Login");

  const changeAuthType = () => {
    authType == "Login" ? setAuthType("Register") : setAuthType("Login");
  };

  return (
    <>
      <Modal
        dismissible
        show={openModal}
        onClose={() => setOpenModal()}
        className="backdrop-blur-sm"
      >
        <div className="bg-gray-900 rounded-lg overflow-hidden">
          <ModalHeader className="bg-gray-900 pt-8">
            {authResponse ? authResponse : authType}
          </ModalHeader>
          <ModalBody className="space-y-6 bg-gray-900">
            {authType == "Login" && <Login></Login>}
            {authType == "Register" && <Register></Register>}
            <a onClick={changeAuthType}>
              {authType == "Login" ? "Register" : "Login"}
            </a>
          </ModalBody>
          <ModalFooter className=" bg-gray-900"></ModalFooter>
        </div>
      </Modal>
    </>
  );
}
