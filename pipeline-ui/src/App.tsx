import { RouterProvider } from "react-router-dom";
import { router } from "./router/router";
// import LoginPage from "./components/LoginPage";

function App() {
  return <RouterProvider router={router} />;
}

export default App;
