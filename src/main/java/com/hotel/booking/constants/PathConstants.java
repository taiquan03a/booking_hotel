package com.hotel.booking.constants;

public class PathConstants {

    public static final String API_V1 = "/api/v1";
    public static final String ORDER = "/order";
    public static final String USER = "/user";
    public static final String PRODUCTS = "/products";
    public static final String API_V1_ADMIN = API_V1 + "/admin";
    public static final String API_V1_AUTH = API_V1 + "/auth";
    public static final String API_V1_ORDER = API_V1 + ORDER;
    public static final String REGISTER = "/register";
    public static final String API_V1_REVIEW = API_V1 + "/review";
    public static final String API_V1_USERS = API_V1 + "/users";
    public static final String API_V1_PRODUCTS = API_V1 + PRODUCTS;
    public static final String ADD = "/add";
    public static final String EDIT = "/edit";
    public static final String CART = "/cart";
    public static final String DELETE_BY_PERFUME_ID = "/delete/{perfumeId}";
    public static final String ORDER_BY_EMAIL = ORDER + "/{userEmail}";
    public static final String ORDER_DELETE = ORDER + "/delete/{orderId}";
    public static final String USER_BY_ID = USER + "/{userId}";
    public static final String USER_ALL = USER + "/all";
    public static final String LOGIN = "/login";

    public static final String INFORMATION = "/information";
    public static final String FORGOT_EMAIL = "/forgot/{email}";
    public static final String RESET = "/reset";
    public static final String CODE = "/{code}";
    public static final String RESET_CODE = RESET + CODE;
    public static final String ACTIVATE_CODE = "/activate" + CODE;
    public static final String EDIT_PASSWORD = "/edit/password";

    public static final String ORDER_ID = "/{orderId}";
    public static final String ORDER_ID_ITEMS = ORDER_ID + "/items";

    public static final String PRODUCT_ID = "/{productId}";

    public static final String IDS = "/ids";
    public static final String SEARCH = "/search";
    public static final String SEARCH_GENDER = SEARCH + "/gender";
    public static final String SEARCH_PRODUCT = SEARCH + "/product";
    public static final String SEARCH_TEXT = SEARCH + "/text";

}
