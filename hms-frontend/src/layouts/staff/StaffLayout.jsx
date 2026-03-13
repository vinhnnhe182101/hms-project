import {Outlet} from 'react-router-dom';
import {StaffHeader} from "./StaffHeader.jsx";


export const StaffLayout = () => {
    return (
            <div className="min-h-screen bg-background">
                {/* Header */}
                <StaffHeader/>

                {/* Content */}
                <main className="mx-auto max-w-7xl px-4 py-6 lg:px-8">
                    <Outlet/>
                </main>
            </div>
    );
};
