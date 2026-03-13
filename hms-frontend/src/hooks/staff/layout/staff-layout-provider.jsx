import {StaffLayoutContext} from "./staff-layout-context.jsx";
import {useDisclosure} from "@mantine/hooks";

export const StaffLayoutProvider = ({children}) => {
    const [isMobileOpen, {toggle, close, open}] = useDisclosure();

    /**
     * @type {StaffLayoutContextType}
     */
    const value = {
        isMobileOpen,
        toggle,
        close,
        open
    }

    return (
            <StaffLayoutContext.Provider value={value}>
                {children}
            </StaffLayoutContext.Provider>
    )
}
