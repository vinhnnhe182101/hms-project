package com.product.hms.utils.page;

import com.product.hms.utils.page.exception.InvalidPageException;
import com.product.hms.utils.page.exception.PageNotFoundException;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageUtils<T> {
    public Page<T> getEntitiesByPage(TypedQuery<T> getEntitiesTypedQuery, TypedQuery<Long> countTypedQuery, Pageable pageable) {
        getEntitiesTypedQuery.setFirstResult((int) pageable.getOffset());
        getEntitiesTypedQuery.setMaxResults(pageable.getPageSize());
        List<T> entities = getEntitiesTypedQuery.getResultList();

        Long total = countTypedQuery.getSingleResult();
        return new PageImpl<>(entities, pageable, total);
    }

    public Pageable getPageable(int index, int size) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }

        return PageRequest.of(index, size);
    }

    public Pageable getPageable(int index, int size, Sort sort) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }

        return PageRequest.of(index, size, sort);
    }

    public void validatePage(Page<T> page, Class<?> entityClass) {
        if (page.isEmpty()) {
            throw new PageNotFoundException(entityClass, page.getNumber(), page.getSize());
        }
    }

    /**
     * Tạo một đối tượng Page từ danh sách
     *
     * @param list     Danh sách cần phân trang
     * @param pageable Thông tin phân trang
     * @param total    Tổng số mục
     * @return Page chứa các mục trong danh sách
     */
    public Page<T> createPage(List<T> list, Pageable pageable, long total) {
        return new PageImpl<>(list, pageable, total);
    }
}
