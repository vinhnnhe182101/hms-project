interface JwtPayload {
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

interface StaffLayoutContextType {
    isMobileOpen: boolean;
    setMobileOpen: React.Dispatch<React.SetStateAction<boolean>>;
}