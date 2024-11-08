package com.hotel.booking.controller;

import com.hotel.booking.service.ZaloPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/zalopay")
@RequiredArgsConstructor
public class ZaloPayController {
    final private ZaloPayService zaloPayService;

    @PostMapping(value = "/create-order")
    public Map<String, Object> createPayment(
            HttpServletRequest request,
            @RequestParam(name = "appuser") String appuser,
            @RequestParam(name = "amount") Long amount,
            @RequestParam(name ="order_id") Long order_id
    ) throws Exception{
        return zaloPayService.createPayment(appuser, amount, order_id);
    }
    @GetMapping(value = "/getstatusbyapptransid")
    public Map<String, Object> getStatusByAppTransId(@RequestParam(name = "apptransid") String apptransid) throws Exception {
        return zaloPayService.getStatusByApptransid(apptransid);
    }
}
