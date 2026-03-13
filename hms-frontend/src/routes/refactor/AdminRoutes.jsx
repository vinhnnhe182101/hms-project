import {Route} from "react-router-dom";
import {ProtectedRoute} from "../ProtectedRoute.jsx";
import {AdminLayout} from "../../layouts/admin/AdminLayout.jsx";
import AdminDashboardPage from "../../pages/admin/DashboardPage.jsx";
import RoomManagementPage from "../../pages/admin/RoomManagementPage.jsx";
import RoomTypesPage from "../../pages/admin/RoomTypesPage.jsx";
import ServiceManagementPage from "../../pages/admin/ServiceManagementPage.jsx";

export const AdminRoutes = (
    <Route
        path="/admin"
        element={
            <ProtectedRoute>
                <AdminLayout/>
            </ProtectedRoute>
        }
    >
        <Route index element={<AdminDashboardPage/>}/>
        <Route path="rooms" element={<RoomManagementPage/>}/>
        <Route path="rooms/types" element={<RoomTypesPage/>}/>
        <Route path="rooms/service" element={<ServiceManagementPage/>}/>
        <Route path="reservations" element={<div>Reservations Management</div>}/>
        <Route path="customers" element={<div>Customers Management</div>}/>
        <Route path="staff" element={<div>Staff Management</div>}/>
        <Route path="payments" element={<div>Payments Management</div>}/>
        <Route path="reports" element={<div>Reports Management</div>}/>
        <Route path="settings" element={<div>Settings</div>}/>
    </Route>
);
