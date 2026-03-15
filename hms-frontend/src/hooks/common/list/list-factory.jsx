import { createContext, useContext, useState } from "react";

/**
 * Hàm tạo context cho list, giúp quản lý trạng thái và các hàm liên quan đến việc hiển thị danh sách.
 * @template PageType, SearchParamsType
 * @param {SearchParamsType} initialSearchParams - Các tham số tìm kiếm ban đầu.
 * @returns {ListFactoryType<PageType, SearchParamsType>} - Một đối tượng chứa Provider và hook useList để sử dụng context.
 */
export const createListContext = (initialSearchParams) => {
    /**
     * @type {React.Context<ListContextType<PageType, SearchParamsType> | null>}
     */
    const Context = createContext(null);

    const Provider = ({ children }) => {
        /**
         * @type {[SearchParamsType, React.Dispatch<React.SetStateAction<SearchParamsType>>]}
         */
        const [searchParams, setSearchParams] = useState(initialSearchParams);

        /**
         * @type {[PageType, React.Dispatch<React.SetStateAction<PageType>>]}
         */
        const [page, setPage] = useState({});

        /**
         * @type {ListContextType<PageType, SearchParamsType>}
         */
        const value = {
            searchParams,
            setSearchParams,
            page,
            setPage,
        };

        return <Context.Provider value={value}>{children}</Context.Provider>;
    };

    const useList = () => {
        const context = useContext(Context);
        if (!context) {
            throw new Error("useList must be used within a ListProvider");
        }
        return context;
    };

    return {
        Provider,
        useList,
    };
};
