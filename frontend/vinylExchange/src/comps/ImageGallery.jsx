import { useState, useEffect } from "react";
import {
  Button,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
} from "flowbite-react";

export default function ImageGallery({ imagePaths, openModal }) {
  const [mainImg, setMainImg] = useState();

  const handleClick = (clickedImg) => {
    setMainImg(clickedImg);
  };

  useEffect(() => {
    if (imagePaths && imagePaths.length > 0) {
      setMainImg(imagePaths[0]);
    }
  }, [imagePaths]);

  function handleModal(url) {
    openModal(url);
  }

  return (
    <div className="w-full">
      {
        <img
          src={
            mainImg ? `http://localhost:8080/${mainImg}` : "/placeholder.png"
          }
          alt="main"
          className="w-130 h-130 object-cover rounded-md mb-4"
          onClick={() =>
            mainImg && handleModal(`http://localhost:8080/${mainImg}`)
          }
        />
      }

      <div className="flex gap-4.5 flex-wrap">
        {imagePaths &&
          imagePaths.map((img, i) => (
            <img
              key={i}
              src={`http://localhost:8080/${img}`}
              onClick={() => handleClick(img)}
              className="w-20 h-20 object-cover cursor-pointer rounded-md border"
            />
          ))}
      </div>
    </div>
  );
}
