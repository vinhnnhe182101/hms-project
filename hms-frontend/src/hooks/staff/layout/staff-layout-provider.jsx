import {StaffLayoutContext} from "./staff-layout-context.jsx";
import {useState} from "react";

export const StaffLayoutProvider = ({children}) => {
    const [isMobileOpen, setMobileOpen] = useState(false);

    /**
     * @type {StaffLayoutContextType}
     */
    const value = {
        isMobileOpen,
        setMobileOpen
    }

    return (
            <StaffLayoutContext.Provider value={value}>
                {children}
            </StaffLayoutContext.Provider>
    )
}
