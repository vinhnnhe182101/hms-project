package com.product.hms.utils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public class PathUtils {
    private PathUtils() {
    }

    public static <Y> Path<Y> getPath(Path<?> path, Class<?> javaType) {
        if (javaType == String.class) {
            return (Path<Y>) path.as(String.class);
        } else if (javaType == Integer.class) {
            return (Path<Y>) path.as(Integer.class);
        } else if (javaType == Long.class) {
            return (Path<Y>) path.as(Long.class);
        } else if (javaType == Double.class) {
            return (Path<Y>) path.as(Double.class);
        } else if (javaType == Float.class) {
            return (Path<Y>) path.as(Float.class);
        } else if (javaType == BigDecimal.class)
            return (Path<Y>) path.as(BigDecimal.class);
        return (Path<Y>) path;
    }

    public static Path<Object> getRealPath(Root<?> root, String fieldName, Map<String, Join<?, ?>> joinMap) {
        if (!fieldName.contains(".")) {
            return root.get(fieldName);
        }
        String[] fieldParts = fieldName.split("\\.");
        String joinKey = root.getJavaType().getSimpleName() + "." + fieldName.substring(0, fieldName.lastIndexOf("."));
        return joinMap.get(joinKey).get(fieldParts[fieldParts.length - 1]);
    }

    public static void join(Root<?> root, String fieldName, JoinType joinType, Map<String, Join<?, ?>> joinMap) {
        if (!fieldName.contains(".")) {
            return;
        }
        if (joinType == null) {
            joinType = JoinType.INNER; // Default to INNER join if not specified
        }
        String[] fieldParts = fieldName.split("\\.");
        Join<?, ?> join;
        StringBuilder joinKey = new StringBuilder();
        joinKey.append(root.getJavaType().getSimpleName()).append(".").append(fieldParts[0]);
        if (!joinMap.containsKey(joinKey.toString())) {
            join = root.join(fieldParts[0], joinType);
            joinMap.put(joinKey.toString(), join);
        }
        for (int i = 1; i < fieldParts.length - 1; i++) {
            String previousKey = joinKey.toString();
            joinKey.append(".").append(fieldParts[i]);
            if (!joinMap.containsKey(joinKey.toString())) {
                join = joinMap.get(previousKey);
                join = join.join(fieldParts[i], joinType);
                joinMap.put(joinKey.toString(), join);
            }
        }
    }
}
