import { useState, useCallback } from "react";
import { useDropzone } from "react-dropzone";

const MAX_FILE_SIZE = 38 * 1024 * 1024; // 38MB in bytes

export default function ImageUploader({ images, setImages }) {
  const [error, setError] = useState(null);

  const onDrop = useCallback(
    (acceptedFiles, rejectedFiles) => {
      console.log("Accepted:", acceptedFiles);
      console.log("Rejected:", rejectedFiles);

      setError(null);

      // Handle rejected files
      if (rejectedFiles.length > 0) {
        const oversizedCount = rejectedFiles.filter((rejection) =>
          rejection.errors.some((err) => err.code === "file-too-large")
        ).length;

        if (oversizedCount > 0) {
          setError(`${oversizedCount} file(s) exceed the 38MB limit`);

          // Auto-clear error after 5 seconds
          setTimeout(() => setError(null), 5000);
        }
      }

      // Handle accepted files
      if (acceptedFiles.length > 0) {
        const newFiles = acceptedFiles.map((file) =>
          Object.assign(file, {
            preview: URL.createObjectURL(file),
          })
        );
        setImages((prev) => [...prev, ...newFiles]);
      }
    },
    [setImages]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { "image/*": [] },
    multiple: true,
    maxSize: MAX_FILE_SIZE,
  });

  const removeImage = (index) => {
    setImages((prev) => prev.filter((_, i) => i !== index));
  };

  return (
    <div className="space-y-4 max-w-100">
      {/* ERROR MESSAGE */}
      {error && (
        <div className="bg-red-900/50 border border-red-500 text-red-200 px-4 py-3 rounded">
          {error}
        </div>
      )}

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
        <p className="text-gray-500 text-sm mt-2">
          Max file size: 38MB per image
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
