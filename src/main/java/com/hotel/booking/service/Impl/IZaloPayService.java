package com.hotel.booking.service.Impl;

import com.hotel.booking.configuration.ZaloPayConfig;
import com.hotel.booking.service.ZaloPayService;
import com.hotel.booking.util.HMACUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IZaloPayService implements ZaloPayService {

    @Override
    public Map<String, Object> createPayment(String appuser, Long amount, Long order_id,String url) throws Exception{
        Map<String, Object> zalopay_Params = new HashMap<>();
        zalopay_Params.put("appid", ZaloPayConfig.APP_ID);
        zalopay_Params.put("apptransid", getCurrentTimeString("yyMMdd") + "_" + new Date().getTime());
        zalopay_Params.put("apptime", System.currentTimeMillis());
        zalopay_Params.put("appuser", appuser);
        zalopay_Params.put("amount", amount);
        zalopay_Params.put("description", "Thanh toan dat phong #" + order_id);
        zalopay_Params.put("bankcode", "");
        String item = "[{\"itemid\":\"knb\",\"itemname\":\"kim nguyen bao\",\"itemprice\":198400,\"itemquantity\":1}]";
        zalopay_Params.put("item", item);
        System.out.println(zalopay_Params.get("apptransid"));
        // embeddata
        // Trong trường hợp Merchant muốn trang cổng thanh toán chỉ hiện thị danh sách
        // các ngân hàng ATM,
        // thì Merchant để bankcode="" và thêm bankgroup = ATM vào embeddata như ví dụ
        // bên dưới
        // embeddata={"bankgroup": "ATM"}
        // bankcode=""
        Map<String, String> embeddata = new HashMap<>();
        embeddata.put("merchantinfo", "eshop123");
        embeddata.put("promotioninfo", "");
        embeddata.put("redirecturl", url);

        Map<String, String> columninfo = new HashMap<String, String>();
        columninfo.put("store_name", "E-Shop");
        embeddata.put("columninfo", new JSONObject(columninfo).toString());
        zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());

        String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
                + zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
                + zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
                + zalopay_Params.get("item");
        zalopay_Params.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));
//		zalopay_Params.put("phone", order.getPhone());
//		zalopay_Params.put("email", order.getEmail());
//		zalopay_Params.put("address", order.getAddress());
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_URL);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }
        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<String, Object>();
        kq.put("apptransid", zalopay_Params.get("apptransid"));
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("orderurl", result.get("orderurl"));
        kq.put("returncode", result.get("returncode"));
        kq.put("zptranstoken", result.get("zptranstoken"));
        kq.put("booking_id",order_id);
        return kq;
    }

    @Override
    public Map<String, Object> getStatusByApptransid(String apptransid) throws Exception{
        String appid = ZaloPayConfig.APP_ID;
        String key1 = ZaloPayConfig.KEY1;
        String data = appid + "|" + apptransid + "|" + key1; // appid|apptransid|key1
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", appid));
        params.add(new BasicNameValuePair("apptransid", apptransid));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder("https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid");
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri.build());

        CloseableHttpResponse res = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<String, Object>();
        kq.put("returncode", result.get("returncode"));
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("isprocessing", result.get("isprocessing"));
        kq.put("amount", result.get("amount"));
        kq.put("discountamount", result.get("discountamount"));
        kq.put("zptransid", result.get("zptransid"));
        return kq;
    }
    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
}
