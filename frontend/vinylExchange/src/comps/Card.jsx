import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import TradeButton from "./Buttons/ModularButton";
import { useCartStore } from "../stores/cartStore";
import { useAuthStore } from "../stores/authStore";
import { useMessagingStore } from "../stores/messagingStore";

import ModularButton from "./Buttons/ModularButton";
import CardImage from "./CardImage";

export default function Card({ item }) {
  return (
    <div className="bg-neutral-primary-soft hover:-translate-y-1 duration-200 ease-in-out border border-default rounded-2xl shadow-xs flex flex-col justify-center items-center py-5 px-5 max-w-70">
      <Link to={`/listing/${item.id}`} className="cursor-pointer">
        <CardImage src={item.imageUrl} alt={item.title} />
      </Link>

      <a href="#">
        <h5 className="mt-3 mb-2 text-2xl font-semibold tracking-tight text-heading ">
          {item.title}
        </h5>
      </a>
      <p className="mb-3 text-body text-amber-400">{item.artist}</p>
      <p className="mb-3 text-body">{item.format || "‎"}</p>
      <p className="text-body mb-2">{item.year || "‎"}</p>
      <p className="text-body">{item.label || "‎"}</p>

      {item.price && (
        <p className="text-body text-xl mt-5 text-green-400">
          {(item.discountedPrice ?? item.price).toLocaleString("tr-TR")} ₺
        </p>
      )}

      <div className="flex flex-row gap-3 justify-center">
        {item.primaryAction && (
          <ModularButton
            listingId={item.id}
            onClickFunction={item.primaryAction.onClick}
            text={item.primaryAction.label}
          />
        )}

        {item.secondaryAction && (
          <ModularButton
            listingId={item.id}
            onClickFunction={item.secondaryAction.onClick}
            text={item.secondaryAction.label}
          />
        )}
      </div>
    </div>
  );
}
