import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import CustomerLayout from './layouts/CustomerLayout';
import HomePage from './pages/customer/HomePage';
import RoomsPage from './pages/customer/RoomsPage';
import RoomDetailPage from './pages/customer/RoomDetailPage';
import ServicesPage from './pages/customer/ServicesPage';
import BookingHistoryPage from './pages/customer/BookingHistoryPage';
import BookingPage from './pages/customer/BookingPage';
import CheckoutPage from './pages/customer/CheckoutPage';
import ServiceCheckoutPage from './pages/customer/ServiceCheckoutPage';
import LoginPage from './pages/auth/LoginPage';

function App() {
    return (
        <Router>
            <Routes>
                {/* Auth routes — không có Header/Footer */}
                <Route path="/login" element={<LoginPage />} />

                {/* Customer routes — có Header + Footer */}
                <Route element={<CustomerLayout />}>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/rooms" element={<RoomsPage />} />
                    <Route path="/rooms/:id" element={<RoomDetailPage />} />
                    <Route path="/services" element={<ServicesPage />} />
                    <Route path="/services/checkout" element={<ServiceCheckoutPage />} />
                    <Route path="/history" element={<BookingHistoryPage />} />
                    <Route path="/booking" element={<BookingPage />} />
                    <Route path="/booking/checkout" element={<CheckoutPage />} />
                </Route>

                {/* TODO: Admin routes — có AdminLayout riêng */}
                {/* <Route element={<AdminLayout />}>
          <Route path="/admin" element={<AdminDashboard />} />
        </Route> */}

                {/* TODO: Housekeeping routes */}
                {/* <Route element={<HousekeepingLayout />}>
          <Route path="/housekeeping" element={<HousekeepingDashboard />} />
        </Route> */}
            </Routes>
        </Router>
    );
}

export default App;
