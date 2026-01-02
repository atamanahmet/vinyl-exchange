import axios from "axios";
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

export default function OrderItemsPage({ orderItems }) {
  const location = useLocation();
  const items = location.state;

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  };
  return (
    <>
      <div class="mt-7 border border-gray-300 rounded-2xl py-4  transition-all duration-200 ease-in-out hover:-translate-y-1 ">
        <svg
          class="my-3 w-full"
          xmlns="http://www.w3.org/2000/svg"
          width="1216"
          height="2"
          viewBox="0 0 1216 2"
          fill="none"
        >
          <path d="M0 1H1216" stroke="#D1D5DB" />
        </svg>

        {items &&
          items.map((item) => (
            <div class="flex max-lg:flex-col items-center gap-8 lg:gap-24 px-3 md:px-11">
              <div class="grid grid-cols-6 w-full">
                <div class="col-span-6 sm:col-span-1">
                  <img
                    src={
                      item.listing.imagePaths[0]
                        ? `http://localhost:8080/${item.listing.imagePaths[0]}`
                        : "/placeholder.png"
                    }
                    alt=""
                    class="h-30 min-w-30 object-cover"
                  />
                </div>
                <div class="col-span-4 sm:col-span-3 max-sm:mt-4 sm:pl-8 flex flex-col justify-center max-sm:items-center ml-7">
                  <h6 class="font-manrope font-semibold text-2xl leading-9 text-indigo-50 mb-3 whitespace-nowrap">
                    <div className="flex gap-2">
                      <p class="font-medium text-left text-lg leading-8 text-indigo-50 whitespace-nowrap">
                        Title:{" "}
                      </p>
                      <p className="font-medium text-left text-lg leading-8 text-indigo-50 whitespace-nowrap w-50 overflow-hidden hover:overflow-auto">
                        {item.listing.title}
                      </p>
                    </div>
                  </h6>
                  <p class="font-medium text-left text-lg leading-8 text-indigo-50 whitespace-nowrap ">
                    Band / Artist : {item.listing.artistName}
                  </p>
                </div>
              </div>
              <div class="flex items-center justify-between gap-10 w-full  sm:pl-28 lg:pl-0">
                <div class="flex flex-col justify-center items-start max-sm:items-center">
                  <p class="font-normal text-lg text-gray-500 leading-8 mb-2 text-left whitespace-nowrap">
                    Quantity
                  </p>
                  <p class=" font-semibold text-lg leading-8 text-green-500 text-center whitespace-nowrap">
                    {item.quantity}
                  </p>
                </div>
                <div class="flex flex-col justify-center items-start max-sm:items-center">
                  <p class="font-normal text-lg text-gray-500 leading-8 mb-2 text-left whitespace-nowrap">
                    Unit price
                  </p>
                  <p class="font-semibold text-lg leading-8 text-green-500 text-left whitespace-nowrap">
                    {item.unitPrice.toLocaleString("tr-TR")} ₺
                  </p>
                </div>
                <div class="flex flex-col justify-center items-start max-sm:items-center">
                  <p class="font-normal text-lg text-gray-500 leading-8 mb-2 text-left whitespace-nowrap">
                    Total price
                  </p>
                  <p class="font-semibold text-lg leading-8 text-green-500 text-left whitespace-nowrap">
                    {item.subTotal.toLocaleString("tr-TR")} ₺
                  </p>
                </div>

                <div class="flex flex-col justify-center items-start max-sm:items-center">
                  <p class="font-normal text-lg text-gray-500 leading-8 mb-2 text-left whitespace-nowrap">
                    Delivery Expected by
                  </p>
                  <p class="font-semibold text-lg leading-8 text-indigo-50 text-left whitespace-nowrap">
                    23rd March 2021
                  </p>
                </div>
              </div>
            </div>

            //   <div class="col-span-4 sm:col-span-3 max-sm:mt-4 sm:pl-8 flex flex-col justify-center max-sm:items-center text-left">
            //     <h6 class="font-manrope font-semibold text-2xl leading-9 text-indigo-50 mb-3 whitespace-nowrap">
            //       {item.listing.title}
            //     </h6>
            //     <p class="font-normal text-lg leading-8 text-gray-500 mb-8 whitespace-nowrap">
            //       {item.listing.artistName}
            //     </p>
            //     <div class="flex items-center max-sm:flex-col gap-x-10 gap-y-3">
            //       <span class="font-normal text-lg leading-8 text-gray-500 whitespace-nowrap">
            //         Qty: {item.quantity}
            //       </span>
            //       <p class="font-semibold text-xl leading-8 text-indigo-50 whitespace-nowrap">
            //         Price: {item.subTotal.toLocaleString("tr-TR")} ₺
            //       </p>
            //     </div>
            //   </div>
          ))}
        <svg
          class="my-3 w-full"
          xmlns="http://www.w3.org/2000/svg"
          width="1216"
          height="2"
          viewBox="0 0 1216 2"
          fill="none"
        >
          <path d="M0 1H1216" stroke="#D1D5DB" />
        </svg>
      </div>
    </>
  );
}
