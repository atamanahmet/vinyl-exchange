import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios";
import ImageGallery from "../comps/ImageGallery";
import { useUser } from "../context/UserContext";

export default function ItemPage({}) {
  const { addToCart } = useUser();

  const location = useLocation();
  const listingId = location.state.id;

  const navigate = useNavigate();

  function addCart() {
    console.log("added to cart");
    addToCart(listing.id);
    // navigate("/cart");
  }
  const [listing, setListing] = useState({
    id: "",
    title: "",
    status: "",
    date: "",
    country: "",
    barcode: "",
    packaging: "",
    format: "",
    trackCount: 0,
    description: "",
    artistName: "",
    tradeValue: 0,
    imagePaths: [],

    tradeable: false,
    price: 0,
    discount: 0,
    tradePreferences: [],
  });

  const normalize = (data) => ({
    id: data.id ?? "",
    title: data.title ?? "",
    status: data.status ?? "",
    date: data.date ?? "",
    description: data.description ?? "",
    country: data.country ?? "",
    barcode: data.barcode ?? "",
    packaging: data.packaging ?? "",
    format: data.format ?? "",
    trackCount: data.trackCount ?? 0,
    artistName: data.artistName ?? "",
    tradeValue: data.tradeValue ?? 0,
    tradeable: data.tradeable ?? false,
    price: data.price ?? 0,
    discount: data.discount ?? 0,
    imagePaths: data.imagePaths ?? [],

    tradePreferences: Array.isArray(data.tradePreferences)
      ? data.tradePreferences.map((p) => ({
          desiredItem: p.desiredItem ?? "",
          paymentDirection: p.paymentDirection ?? "NO_EXTRA",
          extraAmount: p.extraAmount ?? 0,
        }))
      : [],
  });

  async function getListing() {
    try {
      const res = await axios.get(
        `http://localhost:8080/listing/${listingId}`,
        {
          withCredentials: true,
        }
      );
      if (res.status == 200) {
        setListing(normalize(res.data));
      }
    } catch (error) {
      console.log(error);
    }
  }

  useEffect(() => {
    getListing();
  }, []);

  const [mainImage, setMainImage] = useState();
  // `http://localhost:8080/${listing.imagePaths[0]}`

  return (
    <>
      <div className="grid grid-cols-2 gap-10">
        <ImageGallery imagePaths={listing.imagePaths} />
        <div>
          <h2 className="company uppercase text-orange font-bold text-sm sm:text-md tracking-wider pb-3 sm:pb-5">
            sneaker company
          </h2>
          <h3
            // ref={listing.title}
            className="product capitalize text-very-dark-blue font-bold text-3xl sm:text-4xl sm:leading-none pb-3"
          >
            {listing.title}{" "}
            <span className="block lg:mt-1">{listing.format}</span>
          </h3>
          <p className="text-dark-grayish-blue pb-6 lg:py-7 lg:leading-6">
            {listing.description}
          </p>
          <div className="amount font-bold flex items-center justify-between lg:flex-col lg:items-start mb-6">
            <div className="discount-price items-center flex">
              <div className="price text-3xl">
                {listing.discount != 0
                  ? listing.price - (listing.price * listing.discount) / 100
                  : listing.price}{" "}
                ₺
              </div>
              <div className="discount text-green bg-pale-orange w-max px-2 rounded mx-5 h-6">
                {listing.discount != 0 ? listing.discount : null}%
              </div>
            </div>
            <div className="original-price text-grayish-blue line-through lg:mt-2">
              {listing.discount != 0 ? listing.price : null} ₺
            </div>
          </div>
          <div className="sm:flex lg:mt-8 w-full">
            <div className="quantity-container w-full bg-light-grayish-blue rounded-lg h-14 mb-4 flex items-center justify-between px-6 lg:px-3 font-bold sm:mr-3 lg:mr-5 lg:w-1/3">
              <button
                // onClick={listing.format}
                className="text-orange text-2xl leading-none font-bold mb-1 lg:mb-2 lg:text-3xl hover:opacity-60"
              >
                -
              </button>
              <input
                // ref={productQuantityRef}
                min={0}
                max={100}
                // onChange={quantity}
                className="quantity focus:outline-none text-dark-blue bg-light-grayish-blue font-bold flex text-center w-full"
                type="number"
                name="quantity"
                // value={quantityCount}
                aria-label="quantity number"
              />
              <button
                // onClick={quantity}
                className="text-orange text-2xl leading-none font-bold mb-1 lg:mb-2 lg:text-3xl hover:opacity-60"
              >
                +
              </button>
            </div>

            <button
              onClick={addCart}
              className="cart w-full h-14 bg-orange rounded-lg lg:rounded-xl mb-2 shadow-orange-shadow shadow-2xl text-white flex items-center justify-center lg:w-3/5 hover:opacity-60"
            >
              <i className="cursor-pointer text-white text-xl leading-0 pr-3">
                <ion-icon name="cart-outline"></ion-icon>
              </i>
              Add to cart
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
