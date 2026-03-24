import Header from "../DefaultLayout/Header/Header";
import Sidebar from "../DefaultLayout/Sidebar/Sidebar";
import { useSelector } from "react-redux";

import "./DefaultLayout.scss";
import Footer from "./Footer/Footer";
function DefaultLayout({ children }) {
  const { isCollapse } = useSelector((state) => state.appReducer);
   // Check if running in Node.js environment
   if (typeof process !== "undefined" && process.memoryUsage) {
    const memoryUsage = process.memoryUsage();
    console.log(`Heap Used: ${memoryUsage.heapUsed / 1024 / 1024} MB`);
  } else if (typeof performance !== "undefined" && performance.memory) {
    const memoryUsage = performance.memory;
    console.log(`Heap Used: ${memoryUsage.usedJSHeapSize / 1024 / 1024} MB`);
  }
  return (
    <div className="wrapper-default-layout">
      <Header />
      <div className="menu-content">
        <Sidebar />
        <div className={isCollapse ? "container-content-collapse" : "container-content"}>
          {children}
        </div>
      </div>
      <Footer/>
    </div>
  );
}

export default DefaultLayout;
