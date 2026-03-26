import { RouterProvider } from "react-router-dom";
import { router } from "./router/router";
import { AuthProvider, keycloak } from "./context/authContext";
// import { ReactKeycloakProvider } from "@react-keycloak/web";
// import LoginPage from "./components/LoginPage";

function App() {
  return (
    // <ReactKeycloakProvider
    //   authClient={keycloak}
    //   initOptions={{
    //     onLoad: 'login-required',
    //     checkLoginIframe: true  // Bypasses 403 iframe until origins fixed
    //   }}>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>

    // </ReactKeycloakProvider>
  );
}

export default App;
