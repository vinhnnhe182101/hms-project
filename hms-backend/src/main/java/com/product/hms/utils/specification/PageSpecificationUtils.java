package com.product.hms.utils.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class PageSpecificationUtils<T> {
    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Long getTotalElements(Specification<T> specification, Class<T> entityClass, boolean isCountDistinct) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(entityClass);

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.where(predicate);

        if (isCountDistinct) {
            criteriaQuery.select(criteriaBuilder.countDistinct(root));
        } else {
            criteriaQuery.select(criteriaBuilder.count(root));
        }

        return entityManager.createQuery(criteriaQuery).getResultList().stream().mapToLong(Long::longValue).sum();
    }

    private List<T> getContent(Specification<T> specification, Pageable pageable, Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.where(predicate);

        if (!pageable.getSort().isEmpty()) {
            criteriaQuery.orderBy(pageable.getSort().stream()
                    .map(order -> order.isAscending() ?
                            criteriaBuilder.asc(root.get(order.getProperty())) :
                            criteriaBuilder.desc(root.get(order.getProperty())))
                    .toList());
        }

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public Page<T> getPage(Specification<T> specification, Pageable pageable, Class<T> entityClass, boolean isCountDistinct) {
        Long totalElements = getTotalElements(specification, entityClass, isCountDistinct);
        List<T> content = getContent(specification, pageable, entityClass);
        return new PageImpl<>(content, pageable, totalElements);
    }

    public Page<T> getPage(Specification<T> dataSpecification, Specification<T> countSpecification, Pageable pageable, Class<T> entityClass, boolean isCountDistinct) {
        Long totalElements = getTotalElements(countSpecification, entityClass, isCountDistinct);
        List<T> content = getContent(dataSpecification, pageable, entityClass);
        return new PageImpl<>(content, pageable, totalElements);
    }

    public Page<T> getPage(Specification<T> dataSpecification, Specification<T> countSpecification, Pageable pageable, Class<T> entityClass) {
        Long totalElements = getTotalElements(countSpecification, entityClass, false);
        List<T> content = getContent(dataSpecification, pageable, entityClass);
        return new PageImpl<>(content, pageable, totalElements);
    }

    public Page<T> getPage(Specification<T> specification, Pageable pageable, Class<T> entityClass) {
        return getPage(specification, pageable, entityClass, false);
    }
}
