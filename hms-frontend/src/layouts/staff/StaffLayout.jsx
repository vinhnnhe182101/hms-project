import {StaffLayoutProvider} from "../../hooks/staff/layout/staff-layout-provider.jsx";
import {StaffLayoutContent} from "./StaffLayoutContent.jsx";


export const StaffLayout = () => {
    return (
            <StaffLayoutProvider>
                <StaffLayoutContent/>
            </StaffLayoutProvider>
    );
};
