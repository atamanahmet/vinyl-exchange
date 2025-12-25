import { useState, useEffect } from "react";

export default function ImageGallery({ imagePaths }) {
  const [mainImg, setMainImg] = useState();

  const handleClick = (clickedImg) => {
    setMainImg(clickedImg);
  };

  useEffect(() => {
    if (imagePaths && imagePaths.length > 0) {
      setMainImg(imagePaths[0]);
    }
  }, [imagePaths]);

  return (
    <div className="w-full">
      {
        <img
          src={
            mainImg ? `http://localhost:8080/${mainImg}` : "/placeholder.png"
          }
          // onError={(e) => {
          //   e.target.src = "/placeholder.png";
          // }}
          alt="main"
          className="w-auto h-130 object-fit rounded-md mb-4"
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
