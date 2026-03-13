import {useContext} from "react";
import {StaffLayoutContext} from "./staff-layout-context.jsx";

export const useStaffLayout = () => {
    const context = useContext(StaffLayoutContext);

    if (!context) {
        throw new Error("useStaffLayout must be used within a StaffLayoutProvider");
    }
   
    return context;
}
