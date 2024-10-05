package io.github.muhammadredin.tokonyadiaapi.util;

import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PagingUtil {
    public static Pageable getPageable(PagingAndSortingRequest request, Sort sortBy) {
        return PageRequest.of((request.getPage()-1), request.getSize(), sortBy);
    }
}
