import { createBrowserRouter } from "react-router-dom";
import LandingPage from "../pages/LandingPage/LandingPage";
import PipelinePage from "../pages/PipelinePage/PipelinePage";
import PipelineDetailsPage from "../pages/PipelineDetailsPage/PipelineDetailsPage";
import BuildsRunningPage from "../pages/BuildsPage/BuildsRunningPage";
import BuildsListPage from "../pages/BuildsPage/BuildsListPage";

export const router = createBrowserRouter([
  { path: "/", element: <LandingPage /> },
  { path: "/pipelines", element: <PipelinePage /> },
  { path: "/pipelines/:id", element: <PipelineDetailsPage /> },
  {
    path: "/builds/:id",
    element: <BuildsRunningPage />,
  },
  { path: "/builds", element: <BuildsListPage /> },
]);
