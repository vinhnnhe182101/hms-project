import { createContext, useContext, useState, useMemo } from "react";

/**
 * @template T
 * @param {T} initialState - Đối tượng khởi tạo ban đầu
 */
export const createAreaContext = (initialState) => {
    /** @type {React.Context<any>} */
    const Context = createContext(null);

    const AreaProvider = ({ children }) => {
        const [state, setState] = useState(initialState);

        const updateState = (patch) => {
            setState((prev) => ({
                ...prev,
                ...(typeof patch === 'function' ? patch(prev) : patch)
            }));
        };

        const value = useMemo(() => ({
            ...state,
            state,
            setState,
            updateState
        }), [state]);

        return <Context.Provider value={value}>{children}</Context.Provider>;
    };

    /**
     * Hook để truy cập state và các hàm điều khiển
     * @returns {T & { state: T, setState: React.Dispatch<React.SetStateAction<T>>, updateState: (patch: Partial<T> | ((prev: T) => Partial<T>)) => void }}
     */
    const useArea = () => {
        const context = useContext(Context);
        if (!context) throw new Error("useArea must be used within its Provider");
        return context;
    };

    return {
        Provider: AreaProvider,
        useArea: useArea,
    };
};

