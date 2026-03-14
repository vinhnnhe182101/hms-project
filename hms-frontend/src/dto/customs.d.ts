import { LucideIcon } from "lucide-react";
import React from "react";

declare global {
    type JwtPayload = {
        id: number;
        email: string;
        role: string;
        fullName: string;
        provider: string;
        iss?: string;
        sub?: string;
        aud?: string[] | string;
        exp?: number;
        nbf?: number;
        iat?: number;
        jti?: string;
    };

    type StaffLayoutContextType = {
        isMobileOpen: boolean;
        toggle: () => void;
        close: () => void;
        open: () => void;
    };

    type AuthContextType = {
        user: UserResponseDTO | null;
        loading: boolean;
        isAuthenticated: boolean;
        /**
         * @returns Trả về object kết quả login
         */
        login: (
            email: string,
            password: string,
        ) => Promise<{
            success: boolean;
            user?: UserResponseDTO;
            error?: string;
        }>;
        register: (userData: UserRequestDTO) => Promise<{
            success: boolean;
            message?: string;
            data?: UserResponseDTO;
            error?: string;
        }>;
        logout: () => void;
        getDashboardPath: (targetUser: UserResponseDTO | null) => string;
        hasRole: (roles: string | string[]) => boolean;
    };

    type NavItemType = {
        to: string;
        label: string;
        icon: LucideIcon;
        element: React.JSX.Element;
    };
}
