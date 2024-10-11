package com.hotel.booking.util;

import java.io.Serializable;

public enum Status implements Serializable {

    DANG_CHO_XU_LY, // AWAITING PROCESSING
    DANG_XU_LY, // PROCESSING
    HOAN_TAT, //COMPLETED
    DA_GIAO_HANG, //SHIPPED
    DA_BI_HE_THONG_HUY,
    DA_BI_NGUOI_DUNG_HUY,
    DANG_VAN_CHUYEN,
    BI_TU_CHOI,
    DA_HOAN_TIEN,
    YEU_CAU_XAC_MINH_THU_CONG,
    CHO_XAC_NHAN;



    public String toString() {
        switch (this) {
            case DANG_CHO_XU_LY -> {
                return "DANG_CHO_XU_LY";
            }
            case DANG_XU_LY -> {
                return "DANG_XU_LY";
            }
            case HOAN_TAT -> {
                return "HOAN_TAT";
            }

            case DA_GIAO_HANG -> {
                return "DA_GIAO_HANG";
            }

            case DA_BI_HE_THONG_HUY -> {
                return "DA_BI_HE_THONG_HUY";
            }
            case DA_BI_NGUOI_DUNG_HUY -> {
                return "DA_BI_NGUOI_DUNG_HUY";
            }
            case DANG_VAN_CHUYEN -> {
                return "DANG_VAN_CHUYEN";
            }
            case BI_TU_CHOI -> {
                return "BI_TU_CHOI";
            }
            case DA_HOAN_TIEN -> {
                return "DA_HOAN_TIEN";
            }
            case YEU_CAU_XAC_MINH_THU_CONG -> {
                return "YEU_CAU_XAC_MINH_THU_CONG";
            }
            case CHO_XAC_NHAN -> {
                return "CHO_XAC_NHAN";
            }

        }
        return null;
    }
    public String describe() {
        switch (this) {
            case DANG_CHO_XU_LY -> {
                return "Sản phẩm đã được đặt hàng và đang chờ xử lý.";
            }
            case DANG_XU_LY -> {
                return "Sản phẩm đã có ở kho, đang trong quá trình vận chuyển";
            }
            case HOAN_TAT -> {
                return "Đơn hàng đã hoàn tất.";
            }
            case DA_GIAO_HANG -> {
                return "Sản phẩm đã được giao. " +
                        "Vui lòng kiểm tra đơn hàng khi nhận và hoàn tất xác nhận đã mua hàng!";
            }
            case DA_BI_HE_THONG_HUY -> {
                return "Đơn hàng đã bị hệ thống hủy do phương thức thanh toán không hợp lệ " +
                        "hoặc số lượng hàng tồn kho đủ để vận chuyển";
            }

            case DA_BI_NGUOI_DUNG_HUY -> {
                return "Đơn hàng đã bị người dùng hủy";
            }
            case DANG_VAN_CHUYEN -> {
                return "Đơn hàng đang được vận chuyển đến địa chỉ giao hàng.";
            }

            case BI_TU_CHOI -> {
                return "Đơn hàng bị người dùng từ chối nhận";
            }
            case DA_HOAN_TIEN -> {
                return "Đơn hàng đã được hoàn tiền thành công";
            }
            case YEU_CAU_XAC_MINH_THU_CONG -> {
                return "Đơn hàng bị đánh giá là không rõ địa chỉ giao hàng, vui lòng chỉnh sửa lại thông tin địa chỉ thanh toán";
            }
            case CHO_XAC_NHAN -> {
                return "Đơn hàng đã được vận chuyển đến địa chỉ nhận hàng. Vui lòng nhận hàng và xác nhận đơn hàng!";
            }
            default -> {return "Đơn hàng gặp sự cố ngoài ý muốn hoặc trạng thái không xác định";}
        }
    }
}
