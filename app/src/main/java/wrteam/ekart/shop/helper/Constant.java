package wrteam.ekart.shop.helper;

import com.android.volley.toolbox.ImageLoader;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Constant {
    //MODIFICATION PART

    public static final String PROFILE = "profile";
    public static final String UPLOAD_PROFILE = "upload_profile";
    public static String MAINBASEUrl = "https://newekart.wrteam.in/"; //Admin panel url
    //set your jwt secret key here...key must same in PHP and Android
    public static String JWT_KEY = "replace_with_your_strong_jwt_secret_key";
    //MODIFICATION PART END

    public static int GRIDCOLUMN = 3; //Category View Number Of Grid Per Line
    public static int LOAD_ITEM_LIMIT = 10; //Load items limit in listing ,Maximum load item once

    //Do not change anything in this link
    public static String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=";

    public static String BaseUrl = MAINBASEUrl + "api-firebase/";
    public static ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public static String FAQ_URL = BaseUrl + "get-faqs.php";
    public static String SliderUrl = BaseUrl + "slider-images.php";
    public static String CategoryUrl = BaseUrl + "get-categories.php";
    public static String Get_RazorPay_OrderId = BaseUrl + "create-razorpay-order.php";
    public static String SubcategoryUrl = BaseUrl + "get-subcategories-by-category-id.php";
    public static String GET_SECTION_URL = BaseUrl + "sections.php";
    public static String GET_ADDRESS_URL = BaseUrl + "user-addresses.php";
    public static String RegisterUrl = BaseUrl + "user-registration.php";
    public static String PAPAL_URL = MAINBASEUrl + "paypal/create-payment.php";
    public static String LoginUrl = BaseUrl + "login.php";
    public static String GET_ALL_DATA_URL = BaseUrl + "get-all-data.php";
    public static String PRODUCT_SEARCH_URL = BaseUrl + "products-search.php";
    public static String SETTING_URL = BaseUrl + "settings.php";
    public static String GET_PRODUCT_BY_SUB_CATE = BaseUrl + "get-products-by-subcategory-id.php";
    public static String GET_PRODUCT_BY_CATE = BaseUrl + "get-products-by-category-id.php";
    public static String GET_FAVORITES_URL = BaseUrl + "favorites.php";
    public static String GET_OFFLINE_FAVORITES_URL = BaseUrl + "get-products-offline.php";
    public static String GET_OFFLINE_CART_URL = BaseUrl + "get-variants-offline.php";
    public static String GET_PRODUCT_DETAIL_URL = BaseUrl + "get-product-by-id.php";
    public static String CITY_URL = BaseUrl + "get-cities.php";
    public static String GET_AREA_BY_CITY = BaseUrl + "get-areas-by-city-id.php";
    public static String ORDERPROCESS_URL = BaseUrl + "order-process.php";
    public static String USER_DATA_URL = BaseUrl + "get-user-data.php";
    public static String REMOVE_FCM_URL = BaseUrl + "remove-fcm-id.php";
    public static String CART_URL = BaseUrl + "cart.php";
    public static String GET_SIMILAR_PRODUCT_URL = BaseUrl + "get-similar-products.php";
    public static String TRANSACTION_URL = BaseUrl + "get-user-transactions.php";
    public static String PROMO_CODE_CHECK_URL = BaseUrl + "validate-promo-code.php";
    public static String VALIDATE_PROMO_CODE = "validate_promo_code";
    public static String VERIFY_PAYMENT_REQUEST = BaseUrl + "payment-request.php";
    public static String VERIFY_PAYSTACK = "verify_paystack_transaction";
    public static String DISCOUNTED_AMOUNT = "discounted_amount";
    public static String REGISTER_DEVICE = "register-device";
    public static String AccessKey = "accesskey";
    public static String AccessKeyVal = "90336";
    public static String GetVal = "1";
    public static String AUTHORIZATION = "Authorization";
    public static String GET_PRIVACY = "get_privacy";
    public static String GET_TERMS = "get_terms";
    public static String GET_ADDRESSES = "get_addresses";
    public static String DELETE_ADDRESS = "delete_address";
    public static String ADD_ADDRESS = "add_address";
    public static String UPDATE_ADDRESS = "update_address";
    public static String GET_CONTACT = "get_contact";
    public static String GET_ABOUT_US = "get_about_us";
    public static String NEW_BALANCE = "new_balance";
    public static String ADD_TO_FAVORITES = "add_to_favorites";
    public static String REMOVE_FROM_FAVORITES = "remove_from_favorites";
    public static String CANCELLED = "cancelled";
    public static String GET_NOTIFICATIONS = "get-notifications";
    public static String RETURNED = "returned";
    public static String GET_USER_DATA = "get_user_data";
    public static String REMOVE_FCM_ID = "remove_fcm_id";
    public static String KEY_BALANCE = "balance";
    public static String KEY_REFER_EARN_BONUS = "refer-earn-bonus";
    public static String KEY_REFER_EARN_STATUS = "is-refer-earn-on";
    public static String KEY_MAX_EARN_AMOUNT = "max-refer-earn-amount";
    public static String KEY_MIN_WIDRAWAL = "minimum-withdrawal-amount";
    public static String KEY_WALLET_USED = "wallet_used";
    public static String KEY_WALLET_BALANCE = "wallet_balance";
    public static String KEY_VERSION_CODE = "current_version";
    public static String KEY_UPDATE_STATUS = "is-version-system-on";
    public static String KEY_ORDER_RETURN_DAY_LIMIT = "max-product-return-days";
    public static String ADD_MULTIPLE_ITEMS = "add_multiple_items";
    public static String MAX_CART_ITEMS_COUNT = "max_cart_items_count";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String PAYER_EMAIL = "payer_email";
    public static String COUNTRY_CODE = "country_code";
    public static String COUNTRY = "country";
    public static String IS_DEFAULT = "is_default";
    public static String ITEM_NAME = "item_name";
    public static String ITEM_NUMBER = "item_number";
    public static String UPDATE_ORDER_ITEM_STATUS = "update_order_item_status";
    public static String ORDER_ITEM_ID = "order_item_id";
    public static String PAYMENT_METHODS = "payment_methods";
    public static String PAY_M_KEY = "payumoney_merchant_key";
    public static String PAYU_M_ID = "payumoney_merchant_id";
    public static String PAYU_SALT = "payumoney_salt";
    public static String RAZOR_PAY_KEY = "razorpay_key";
    public static String share_url = MAINBASEUrl;
    public static String REFER_EARN_BONUS = "";
    public static String MAX_EARN_AMOUNT = "";
    public static String paystack_public_key = "paystack_public_key";
    public static String UNREAD_NOTIFICATION_COUNT = "unread_notification_count";
    public static String UNREAD_WALLET_COUNT = "unread_wallet_count";
    public static String UNREAD_TRANSACTION_COUNT = "unread_transaction_count";
    public static String flutterwave_public_key = "flutterwave_public_key";
    public static String flutterwave_secret_key = "flutterwave_secret_key";
    public static String flutterwave_encryption_key = "flutterwave_encryption_key";

//    public static String stripe_publishable_key = "stripe_publishable_key";
//    public static String STRIPE_SECRET_KEY = "stripe_secret_key";
//    public static String STRIPE_CURRENCYz = "inr";
//    public static String STRIPE_BASE_URL = "https://netsofters.com/stripe/";
//
//    public static String midtrans_merchant_id = "midtrans_merchant_id";
//    public static String midtrans_client_key = "midtrans_client_key";
//    public static String midtrans_server_key = "midtrans_server_key";
//    public static String MIDTRANS_MERCHANT_BASE_URL = "https://netsofters.com/charge/index.php/";

    public static String CITY_ID = "city_id";
    public static String AREA_ID = "area_id";
    public static String REFERRAL_CODE = "referral_code";
    public static String FRIEND_CODE = "friends_code";
    public static String VERSION_CODE;
    public static String TOOLBAR_TITLE;
    public static String REQUIRED_VERSION;
    public static String VERSION_STATUS;
    public static String SOLDOUT_TEXT = "Sold Out";
    public static String QTY = "qty";
    public static String GET_USER_CART = "get_user_cart";
    public static String CART_ITEM_COUNT = "cart_count";
    public static String GET_USER_TRANSACTION = "get_user_transactions";
    public static String TYPE_TRANSACTION = "transactions";
    public static String TYPE_WALLET_TRANSACTION = "wallet_transactions";
    public static String SUCCESS = "success";
    public static String CREDIT = "credit";
    public static HashMap<String, String> CartValues = new HashMap<>();
    public static boolean CLICK = false;
    public static String IS_FAVORITE = "is_favorite";
    public static String REMOVE_FROM_CART = "remove_from_cart";
    public static String selectedAddressId = "";
    public static int selectedDatePosition = 0;
    public static DecimalFormat formater = new DecimalFormat("0.00");
    public static int MAX_PRODUCT_LIMIT = 0;
    public static String SORT = "sort";
    public static String TYPE = "type";
    public static String IMAGE = "image";
    public static String NAME = "name";
    public static String TYPE_ID = "type_id";
    public static String ID = "id";
    public static String SUBTITLE = "subtitle";
    public static String PRODUCTS = "products";
    public static String SUC_CATE_ID = "subcategory_id";
    public static String DESCRIPTION = "description";
    public static String STATUS = "status";
    public static String DATE_ADDED = "date_added";
    public static String TITLE = "title";
    public static String SECTION_STYLE = "style";
    public static String SHORT_DESC = "short_description";
    public static String REGISTER = "register";
    public static String EMAIL = "email";
    public static String MOBILE = "mobile";
    public static String ALTERNATE_MOBILE = "alternate_mobile";
    public static String PASSWORD = "password";
    public static String FCM_ID = "fcm_id";
    public static String PINCODE = "pincode";
    public static String STATE = "state";
    public static String ERROR = "error";
    public static String VERIFY_USER = "verify-user";
    public static String GET_SLIDER_IMAGE = "get-slider-images";
    public static String USER_ID = "user_id";
    public static String OTP = "otp";
    public static String ADD_WALLET_BALANCE = "add_wallet_balance";
    public static String TAX_AMOUNT = "tax_amount";
    public static String TAX_PERCENT = "tax_percentage";
    public static String EDIT_PROFILE = "edit-profile";
    public static String CHANGE_PASSWORD = "change-password";
    public static String CATEGORY_ID = "category_id";
    public static String CATEGORIES = "categories";
    public static String SLIDER_IMAGES = "slider_images";
    public static String SECTIONS = "sections";
    public static String OFFER_IMAGES = "offer_images";
    public static String INDICATOR = "indicator";
    public static String MADE_IN = "made_in";
    public static String MANUFACTURER = "manufacturer";
    public static String RETURN_STATUS = "return_status";
    public static String CANCELLABLE_STATUS = "cancelable_status";
    public static String ROW_ORDER = "row_order";
    public static String TILL_STATUS = "till_status";
    public static String SUB_CATEGORY_ID = "subcategory_id";
    public static String GET_FAVORITES = "get_favorites";
    public static String GET_FAVORITES_OFFLINE = "get_products_offline";
    public static String GET_CART_OFFLINE = "get_variants_offline";
    public static String PRODUCT_SEARCH = "products-search";
    public static String SEARCH = "search";
    public static String Add_TRANSACTION = "add_transaction";
    public static String GET_PAYMENT_METHOD = "get_payment_methods";
    public static String GET_ORDERS = "get_orders";
    public static String CONTACT = "contact";
    public static String DATA = "data";
    public static String VARIANT = "variants";
    public static String PRODUCT_ID = "product_id";
    public static String GET_SIMILAR_PRODUCT = "get_similar_products";
    public static String PRODUCT_IDs = "product_ids";
    public static String VARIANT_IDs = "variant_ids";
    public static String MEASUREMENT = "measurement";
    public static String MEASUREMENT_UNIT_ID = "measurement_unit_id";
    public static String PRICE = "price";
    public static String DISCOUNT = "discount";
    public static String DISCOUNTED_PRICE = "discounted_price";
    public static String SERVE_FOR = "serve_for";
    public static String STOCK = "stock";
    public static String STOCK_UNIT_ID = "stock_unit_id";
    public static String MEASUREMENT_UNIT_NAME = "measurement_unit_name";
    public static String STOCK_UNIT_NAME = "stock_unit_name";
    public static String SETTINGS = "settings";
    public static String GET_SETTINGS = "get_settings";
    public static String MINIMUM_ORDER_AMOUNT = "min_order_amount";
    public static String GET_TIME_SLOT_CONFIG = "get_time_slot_config";
    public static String TIME_SLOT_CONFIG = "time_slot_config";
    public static String IS_TIME_SLOTS_ENABLE = "is_time_slots_enabled";
    public static String DELIVERY_STARTS_FROM = "delivery_starts_from";
    public static String ALLOWED_DAYS = "allowed_days";
    public static String paypal_method = "paypal_payment_method";
    public static String payu_method = "payumoney_payment_method";
    public static String razor_pay_method = "razorpay_payment_method";
    public static String cod_payment_method = "cod_payment_method";
    public static String paystack_method = "paystack_payment_method";
    public static String flutterwave_payment_method = "flutterwave_payment_method";
    public static String midtrans_payment_method = "midtrans_payment_method";
    public static String stripe_payment_method = "stripe_payment_method";
    public static String KEY_REFER_EARN_METHOD = "refer-earn-method";
    public static String KEY_MIN_REFER_ORDER_AMOUNT = "min-refer-earn-order-amount";
    public static String REFER_EARN_ORDER_AMOUNT = "";
    public static String REFER_EARN_METHOD = "";
    public static String REFER_EARN_ACTIVE = "";
    //settings
    public static Double SETTING_DELIVERY_CHARGE = 0.0;
    public static Double SETTING_TAX = 0.0;
    public static Double SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = 0.0;
    public static Double SETTING_MINIMUM_ORDER_AMOUNT = 0.0;
    public static Double SETTING_AREA_WISE_DELIVERY_CHARGE = 0.0;
    public static String SETTING_CURRENCY_SYMBOL = "";
    public static String SETTING_MAIL_ID = "";
    public static Double WALLET_BALANCE = 0.0;
    public static Double MINIMUM_WITHDRAW_AMOUNT = 0.0;
    public static int ORDER_DAY_LIMIT = 0;
    public static String country_code = "";
    public static String U_ID = "";
    public static String REPLY_TO = "reply_to";
    public static String MINIMUM_AMOUNT = "min_amount";
    public static String DELIEVERY_CHARGE = "delivery_charge";
    public static String AREA_WISE_DELIVERY_CHARGE = "area-wise-delivery-charge";
    public static String CURRENCY = "currency";
    public static String TAX = "tax";
    public static String GET_FAQS = "get_faqs";
    public static String LIMIT = "limit";
    public static String OFFSET = "offset";
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String ACTIVE_STATUS = "active_status";

    public static String OTHER_IMAGES = "other_images";
    public static String AMOUNT = "amount";
    public static String REFERENCE = "reference";
    public static String PROMO_DISCOUNT = "promo_discount";
    public static String DISCOUNT_AMT = "discount_rupees";
    public static String TOTAL = "total";
    public static String TOTAL_AMOUNT = "total_amount";
    public static double FLOAT_TOTAL_AMOUNT = 0;
    public static int TOTAL_CART_ITEM = 0;
    public static String PRODUCT_VARIANT_ID = "product_variant_id";
    public static String QUANTITY = "quantity";
    public static String USER_NAME = "user_name";
    public static String DELIVERY_CHARGE = "delivery_charge";
    public static String DELIVERY_TIME = "delivery_time";
    public static String PAYMENT_METHOD = "payment_method";
    public static String ADDRESS = "address";
    public static String LANDMARK = "landmark";
    public static String TRANS_ID = "txn_id";
    public static String MESSAGE = "message";
    public static String FINAL_TOTAL = "final_total";
    public static String FROM = "from";
    public static String GETORDERS = "get_orders";
    public static String ORDER_ID = "order_id";
    public static String UPDATE_ORDER_STATUS = "update_order_status";
    public static String PLACE_ORDER = "place_order";
    public static String NEW = "new";
    public static String OLD = "old";
    public static String HIGH = "high";
    public static String LOW = "low";

    public static String SUB_TOTAL = "sub_total";
    public static String DELIVER_BY = "deliver_by";
    public static String UNIT = "unit";
    public static String SLUG = "slug";
    public static String PROMO_CODE = "promo_code";
    public static String TOKEN = "token";
    public static boolean isOrderCancelled;
    public static CharSequence[] filtervalues = {" Newest to Oldest ", " Oldest to Newest ", " Price Highest to Lowest ", " Price Lowest to Highest "};
    public static String FRND_CODE = "";

    public static String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghjiklmnopqrstuvwxyz";
    public static String NUMERIC_STRING = "123456789";

    public static String verificationCode;
    public static String PAYPAL = "";
    public static String PAYUMONEY = "";
    public static String RAZORPAY = "";
    public static String COD = "";
    public static String PAYSTACK = "";
    public static String FLUTTERWAVE = "";
    public static String STRIPE = "";
    public static String MIDTRANS = "";

    public static String MERCHANT_ID = "";
    public static String MERCHANT_KEY = "";
    public static String MERCHANT_SALT = "";
    public static String RAZOR_PAY_KEY_VALUE = "";
    public static String PAYSTACK_KEY = "";

    public static String FLUTTERWAVE_PUBLIC_KEY_VAL = "";
    public static String FLUTTERWAVE_SECRET_KEY_VAL = "";
    public static String FLUTTERWAVE_ENCRYPTION_KEY_VAL = "";

    public static String STRIPE_PUBLISHABLE_KEY = "";
    public static String STRIPE_SECRET_KEY = "";
    public static String STRIPE_CURRENCY = "inr";
    public static String STRIPE_BASE_URL = BaseUrl + "/stripe/"; //https://netsofters.com/stripe/";

    public static String MIDTRANS_CLIENT_KEY = "SB-Mid-client-48IA7XuBPRpC1s-F";
    public static String MIDTRANS_MERCHANT_BASE_URL = BaseUrl + "https://netsofters.com/charge/index.php/";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String randomNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * NUMERIC_STRING.length());
            builder.append(NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
