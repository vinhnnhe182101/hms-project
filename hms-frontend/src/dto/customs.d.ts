import {LucideIcon} from "lucide-react";
import React from "react";

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
}

type StaffLayoutContextType = {
    isMobileOpen: boolean;
    toggle: () => void;
    close: () => void;
    open: () => void;
}

type NavItemType = {
    to: string;
    label: string;
    icon: LucideIcon;
    element: React.JSX.Element;
}