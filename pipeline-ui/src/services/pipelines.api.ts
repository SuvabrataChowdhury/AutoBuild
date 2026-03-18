/* eslint-disable @typescript-eslint/no-explicit-any */
// Temporary API base URL, this needs to be configured properly
// TODO: Move to environment variable
const API_BASE_URL = "http://localhost:8080/api/v1";

export function getLiveBuildUpdates(id: string): string {
    return `${API_BASE_URL}/pipeline/build/sse/subscribe/${id}`
}

async function errorHandler(response: any) : Promise<string[]> {
   if(response.status === 401) {
         //handle unauthorized access
        window.location.href = "/login";
        return ["Unauthorized access. Redirecting to login."];
    }
    else if(!response.ok) {
        const detail = response.data.detail || "An error occurred";
        const message = detail.split(",")
        return message;
    }
    return response.data;
}




//TODO : on start build, send an execute call to backend

