import { useState, useCallback } from "react";
import { useDropzone } from "react-dropzone";

export default function ImageUploader({ images, setImages }) {
  const onDrop = useCallback(
    (acceptedFiles) => {
      const newFiles = acceptedFiles.map((file) =>
        Object.assign(file, {
          preview: URL.createObjectURL(file),
        })
      );
      setImages((prev) => [...prev, ...newFiles]);
    },
    [setImages]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { "image/*": [] },
    multiple: true,
  });

  const removeImage = (index) => {
    setImages((prev) => prev.filter((_, i) => i !== index));
  };

  return (
    <div className="space-y-4 max-w-100">
      {/* DROPZONE */}
      <div
        {...getRootProps()}
        className={`
          border-2 border-dashed rounded-xl p-8 text-center cursor-pointer 
          transition
          ${isDragActive ? "bg-neutral-800 border-white" : "border-neutral-700"}
        `}
      >
        <input {...getInputProps()} />

        <p className="text-gray-300">
          {isDragActive
            ? "Drop images here…"
            : "Drag & drop or click to upload images"}
        </p>
      </div>

      {/* PREVIEW GRID */}
      {images.length > 0 && (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
          {images.map((img, index) => (
            <div key={index} className="relative group">
              <img
                src={img.preview}
                alt="preview"
                className="w-max h-30 object-cover rounded-lg "
              />

              {/* REMOVE BUTTON */}
              <button
                onClick={() => removeImage(index)}
                className="
                  absolute top-0.5 right-0.5 
                  w-10 h-10
                  bg-indigo-600 hover:bg-red-700 
                  text-white p-0 rounded-full
                  opacity-50 group-hover:opacity-100 transition
                flex items-center justify-center
                "
              >
                X
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
