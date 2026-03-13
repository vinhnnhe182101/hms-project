import {Route} from "react-router-dom";
import {ProtectedRoute} from "../ProtectedRoute.jsx";
import {StaffLayout} from "../../layouts/staff/StaffLayout.jsx";

export const StaffRoutes = () => {
    return (
            <Route path={"/staff"} element={<ProtectedRoute children={<StaffLayout/>}/>}>
                <Route index element={<div>Staff Dashboard</div>}/>
            </Route>
    )
}
