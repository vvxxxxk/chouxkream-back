package com.kream.chouxkream.bid.controller;

import com.kream.chouxkream.bid.service.BidService;
import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    @GetMapping("/buy/{product_no}")
    public ResponseEntity<ResponseMessageDto> getBuyBidFromData(@PathVariable("product_no") Long productNo,
                                                                @RequestParam("size_no") Long productSizeNo) {

        System.out.println("productNo = " + productNo);
        System.out.println("productSizeNo = " + productSizeNo);

        return null;
    }
}
