import { useNavigate, useLocation, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios";
import ImageGallery from "../comps/ImageGallery";
import { useUser } from "../context/UserContext";
import { useCart } from "../context/CartContext";

import {
  Button,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
} from "flowbite-react";

export default function ItemPage() {
  const location = useLocation();
  const { user } = useUser();
  const { listingId } = useParams();
  const [listing, setListing] = useState();
  const [mainImage, setMainImage] = useState();
  const { addToCart } = useCart();
  const [imageUrlForModal, setImageUrlForModal] = useState();
  const [openModal, setOpenModal] = useState(false);
  const [openModalUrl, setOpenModalUrl] = useState();
  const [isOwnerUser, setIsOwnerUser] = useState();
  const [loading, setLoading] = useState(true);

  function openModalImage(url) {
    console.log(url);
    setOpenModal((curr) => !curr);
    setOpenModalUrl(url);
  }

  let label;
  let format;

  const navigate = useNavigate();

  const navigateItemWithId = () => {
    navigate(`/listing/${listing.id}`);
  };
  const navigateEditItemWithId = () => {
    navigate(`/edit/${listing.id}`);
  };
  const navigateMessagingWithItemId = () => {
    navigate(`/messaging/${listing.id}`);
  };

  function addCart() {
    console.log("added to cart");
    addToCart(listing.id);
    // navigate("/cart");
  }

  async function getListing() {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/listings/${listingId}`,
        {
          withCredentials: true,
        }
      );
      if (res.status == 200) {
        console.log(res.data);
        setListing(res.data);
      }
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    getListing();
    console.log(user);
  }, [listingId]);

  useEffect(() => {
    if (listing && user && listing.ownerUsername === user.username) {
      setIsOwnerUser(true);
    } else {
      setIsOwnerUser(false);
    }
  }, [listing, user]);

  useEffect(() => {
    // if (listing.ownerUsername == user.username) {
    //   setIsOwnerUser(true);
    //   console.log("is user");
    // }
    // switch (listing.format) {
    //   case "33":
    //     format = `12" LP - 33 RPM`;
    //     break;
    //   case "45":
    //     format = `7" EP - 45 RPM`;
    //     break;
    //   default:
    //     "";
    //     break;
    // }
    // if (listing.labelName) {
    //   if (listing.labelName.includes("no label")) {
    //     label = "";
    //   } else {
    //     if (listing.labelName.includes("Records")) {
    //       label = listing.labelName;
    //     } else {
    //       label = listing.labelName + " Records";
    //     }
    //   }
    // }
  }, [loading]);

  return (
    <>
      {listing && (
        <div className="fixedRoot">
          <div>
            <Modal
              dismissible
              show={openModal}
              onClose={() => setOpenModal(false)}
              className="backdrop-blur-sm"
            >
              <div className="bg-gray-900 rounded-lg overflow-hidden">
                <ModalHeader className="bg-gray-900 py-5 px-6"></ModalHeader>
                <ModalBody className="space-y-6 bg-gray-900">
                  <img src={openModalUrl} alt="" className="h-full w-full" />
                </ModalBody>
                <ModalFooter className=" bg-gray-900"></ModalFooter>
              </div>
            </Modal>
          </div>
          <div className="grid grid-cols-2 gap-10 text-left">
            <ImageGallery
              imagePaths={listing.imagePaths}
              openModal={openModalImage}
            />
            <div>
              <h2 className="company uppercase text-orange font-bold text-sm sm:text-md tracking-wider pb-3 sm:pb-5">
                {listing.labelName}
              </h2>
              <h3
                // ref={listing.title}
                className="product capitalize text-very-dark-blue font-bold text-3xl sm:text-4xl sm:leading-none pb-3"
              >
                {listing.title}{" "}
                <span className="block  mt-5 text-2xl text-indigo-400">
                  {/* {size} */}
                  {listing.artistName}
                </span>
                <span className="block  mt-5 text-2xl">
                  {/* {size} */}
                  {label}
                </span>
                <span className="block  mt-5 text-2xl">{format}</span>
                <span className="block  mt-5 text-2xl">
                  <span className="italic font-normal text-amber-200">
                    Condition:{" "}
                  </span>
                  {listing.condition}
                </span>
              </h3>
              <p className="text-dark-grayish-blue pb-6 lg:py-7 lg:leading-6">
                {listing.description}
              </p>
              <div className="amount font-bold flex items-center justify-between lg:flex-col lg:items-start mb-6">
                <div className="discount-price items-center flex">
                  <div className="price text-3xl ">
                    <span className="text-green-400">
                      {listing.discount == 0
                        ? listing.price.toLocaleString("tr-TR") + " ₺"
                        : listing.discountedPrice.toLocaleString("tr-TR") +
                          " ₺"}
                    </span>
                  </div>
                  <div className="discount text-green bg-pale-orange w-max px-2 rounded mx-5 h-6">
                    {listing.discount != 0 ? listing.discount + "%" : null}
                  </div>
                </div>
                <div className="original-price text-grayish-blue line-through lg:mt-2">
                  {listing.discount != 0
                    ? listing.price.toLocaleString("tr-TR") + "₺"
                    : null}
                </div>
              </div>
              <div className="sm:flex lg:mt-8 w-full">
                {!isOwnerUser && (
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
                )}
                {!isOwnerUser && (
                  <div className="inline-flex justify-center w-full gap-3 items-center -mt-5">
                    <button
                      onClick={() => {
                        navigateMessagingWithItemId();
                      }}
                      className=" w-full h-14 bg-orange rounded-lg lg:rounded-xl mb-2 hover:shadow-lg hover:shadow-indigo-600 text-white flex bg-indigo-600 hover:bg-indigo-800 hover:text-slate-200 items-center justify-center lg:w-3/5 "
                      // className="rounded-lg py-2 px-3 text-amber-50  "
                    >
                      <i className="cursor-pointer text-white text-xl text-center ">
                        <ion-icon name="cart-outline"></ion-icon>
                      </i>
                      Trade
                    </button>
                    <button
                      onClick={addCart}
                      className=" w-full h-14 bg-orange rounded-lg lg:rounded-xl mb-2 hover:shadow-lg hover:shadow-indigo-600 text-white flex bg-indigo-600 hover:bg-indigo-800 hover:text-slate-200 items-center justify-center lg:w-3/5 "
                      // className="rounded-lg py-2 px-3 text-amber-50  "
                    >
                      <i className="cursor-pointer text-white text-xl text-center ">
                        <ion-icon name="cart-outline"></ion-icon>
                      </i>
                      Add to cart
                    </button>
                  </div>
                )}
                {isOwnerUser && (
                  <div className="inline-flex  w-full gap-3 items-center mt-5">
                    <button
                      onClick={() => {
                        navigateEditItemWithId();
                      }}
                      className=" w-full h-14 bg-orange rounded-lg lg:rounded-xl mb-2 hover:shadow-lg hover:shadow-indigo-600 text-white flex bg-indigo-600 hover:bg-indigo-800 hover:text-slate-200 items-center justify-center lg:w-3/5 "
                      // className="rounded-lg py-2 px-3 text-amber-50  "
                    >
                      <i className="cursor-pointer text-white text-xl text-center ">
                        <ion-icon name="cart-outline"></ion-icon>
                      </i>
                      Edit
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
