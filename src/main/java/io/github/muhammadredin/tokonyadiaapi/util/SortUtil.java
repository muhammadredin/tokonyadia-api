package io.github.muhammadredin.tokonyadiaapi.util;

import org.springframework.data.domain.Sort;

public class SortUtil {
    public static Sort getSort(String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.unsorted();
        }
        return sortBy.startsWith("-") ?
                Sort.by(Sort.Direction.DESC, sortBy.substring(1)) :
                Sort.by(Sort.Direction.ASC, sortBy);
    }
}
