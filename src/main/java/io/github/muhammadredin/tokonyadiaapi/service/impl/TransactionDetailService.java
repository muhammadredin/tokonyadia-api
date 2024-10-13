package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.entity.TransactionDetail;
import io.github.muhammadredin.tokonyadiaapi.repository.TransactionDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionDetailService {
    private final TransactionDetailRepository transactionDetailRepository;

    public void createTransactionDetail(Long transactionId) {}
}
