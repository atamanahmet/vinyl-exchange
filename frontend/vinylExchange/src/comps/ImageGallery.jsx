import { useState, useEffect } from "react";

export default function ImageGallery({ imagePaths }) {
  const [mainImg, setMainImg] = useState();

  const handleClick = (clicked, index) => {
    setMainImg(clicked);
  };
  useEffect(() => {
    if (imagePaths && imagePaths.length > 0) {
      setMainImg(imagePaths[0]);
    }
  }, [imagePaths]);

  return (
    <div className="w-full">
      <img
        src={`http://localhost:8080/${mainImg}`}
        onError={(e) => {
          e.target.src = "/placeholder.png";
        }}
        alt="main"
        className="w-200 h-100 object-cover rounded-md mb-4"
      />

      <div className="flex gap-4.5 flex-wrap">
        {imagePaths.map((img, i) => (
          <img
            key={i}
            src={`http://localhost:8080/${img}`}
            onClick={() => handleClick(img, i)}
            className="w-20 h-20 object-cover cursor-pointer rounded-md border"
          />
        ))}
      </div>
    </div>
  );
}
