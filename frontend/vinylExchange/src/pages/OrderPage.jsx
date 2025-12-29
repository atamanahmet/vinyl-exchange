import axios from "axios";
import { useEffect, useState } from "react";

export default function OrderPage() {
  const [orders, setOrders] = useState();

  async function fetchOrders() {
    try {
      const res = await axios.get("http://localhost:8080/order", {
        withCredentials: true,
      });

      if (res.status == 200) {
        console.log(res.data);
        setOrders(res.data);
      }
    } catch (error) {
      console.log(error);
    }
  }

  useEffect(() => {
    fetchOrders();
  }, []);
  return (
    <>
      {/* <div>
        {orders &&
          orders.map((order) => {
            <p>{order.id}</p>;

            {
              orders.orderItems.map((orderItem) => {
                <p>{orderItem.listing.title}</p>;
              });
            }
          })}
      </div> */}
    </>
  );
}
