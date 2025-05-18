package com.hotel.booking.service;

import java.util.Map;

public interface ZaloPayService {
    Map<String, Object> createPayment(String appuser,Long amount,Long order_id,String url) throws Exception;
    Map<String,Object> getStatusByApptransid(String apptransid) throws Exception;
}
