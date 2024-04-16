package com.kream.chouxkream.bid.service;

import com.kream.chouxkream.bid.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
}
