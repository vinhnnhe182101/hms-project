// id: decoded.id,
//     email: decoded.sub || decoded.email,
//     role: decoded.role,
//     fullName: decoded.fullName,
//     provider: decoded.provider,


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