package com.pcamarounds.constants;

import com.pcamarounds.R;
import com.pcamarounds.controller.RealmController;
import com.pcamarounds.retrofit.Environment;

public interface AppConstants {

    /********************MUST HAVE PARAMETERS***********************/
    String DEVICE_TYPE = "device_type";
    String DEVICE_ID = "device_id";
    String API_KEY = "api_key";
    String API_KEY_VALUE = "tocam@user#2";
    String ACCESS_TOKEN = "access_token";
    String DEVICE_TYPE_VALUE = "1";
    String TYPE = "type";
    String MYTYPE = "pharma";
    String ROLE = "role";
    String ROLE_VALUE = "provider";
    String LEAD_TYPE = "lead_type";
    String PAGE = "page";
    String SEARCH_TYPE = "search_type";
    String FLAG = "flag";
    String SPLASH = "splash";
    String LOGIN = "login";
    String CODE = "code";
    String COUNTRY_CODE = "country_code";
    String COMET_ID = "comet_id";
    //String CAMAROUND = "camaround";
    String QUE = "que";
    String ANS = "ans";
    String EMAIL = "email";
    String PASSWORD = "password";
    String FIRST_NAME = "first_name";
    String LAST_NAME = "last_name";
    String NAME = "name";
    String MOBILE = "mobile";
    String ACCESS = "access";
    String AMOUNT = "amount";
    String UID = "uid";
    String NOTIFICATION_ON = "notification_on";
    String EMAIL_ON = "email_on";
    String CUSTOMER_ID = "customer_id";
    String CATEGORY_ID = "category_id";
    String CATEGORY = "category";
    String DOB = "date_of_birth";
    String GENDER = "gender";
    String USER_ID = "user_id";
    String SERVICE_TYPE = "service_type";
    String PARTNER_ID = "partner_id";
    String CCNUM = "ccNum";
    String CVV = "cvv";
    String CCEXP = "ccExp";
    String BANKS_ID = "banks_id";
    String wallet_amount = "wallet_amount";
    String CUSTOMER_MOBILE_ID = "customer_mobile_id";
    String CUSTOMER_BOOKING_ID = "customer_booking_id";
    String SKILLS = "tech_skills";
    String HOBBIES = "tech_hobbies";
    String FAV_COLOR = "tech_fav_color";
    String EYE_COLOR = "tech_eye_color";
    String ZODIAC_SIGN = "tech_zodiac_sign";
    String FAV_FOOD = "tech_fav_food";
    String HEIGHT = "tech_height";
    String DESCRIPTION = "tech_decription";
    String HAIR_COLOR = "tech_hair_color";
    String SEARCH = "search";
    String LASTORDER = "lastOrder";
    String DETAILS = "details";
    String ENQUIRY_TYPE = "enquiry_type";
    String TECH_LATE_TIME = "tech_late_time";
    String FLAGLOGIN = "flaglogin";
    String GUEST = "guest";
    String IMAGES = "images[]";
    String ABOUTUS = "aboutus";
    String FRAGMENTFROM = "fragmentFrom";
    String AUTHENTICATION = "authentication";
    String ACC_HOLDER = "acc_holder";
    String ACC_NUMBER = "acc_number";
    String ACC_BANK = "acc_bank";
    String ACC_TYPE = "acc_type";
    String BANK_ID = "bank_id";
    String ROUTE_NO = "route_no";
    String RADIOUS = "radious";
    String JOB_STATUS = "job_status";

    /**********************OTHER PARAMETERS*******************************************/


    /**
     * Error Messages
     */
    String password_empty = "Please enter your password";
    String password_error = "The password must be at least 8 characters";
    String email_empty = "Please enter your Email Id";
    String email_error = "Please enter valid Email Id";

    String name_empty = "Please enter Name";
    String name_error = "Please enter valid Name";

    String mobile_empty = "Please enter Mobile number";
    String mobile_error = "Mobile no. should be 8 digit long";

    String[] heightArray = {"153 CM (5'02)", "154 CM (5'05)", "155 CM (5'09)", "156 CM (5'12)", "157 CM (5'15)",
            "158 CM (5'18)", "159 CM (5'22)", "160 CM (5'25)", "161 CM (5'28)",
            "162 CM (5'31)", "163 CM (5'35)", "164 CM (5'38)", "165 CM (5'41)",
            "166 CM (5'45)", "167 CM (5'48)", "168 CM (5'51)", "169 CM (5'54)",
            "170 CM (5'58)", "171 CM (5'61)", "172 CM (5'64)", "173 CM (5'68)",
            "174 CM (5'71)", "175 CM (5'74)", "176 CM (5'77)", "177 CM (5'81)",
            "178 CM (5'84)", "179 CM (5'87)", "180 CM (5'91)", "181 CM (5'94)",
            "182 CM (5'97)", "183 CM (6'00)", "184 CM (6'04)", "185 CM (6'07)",
            "186 CM (6'10)", "187 CM (6'14)", "188 CM (6'17)", "189 CM (6'20)",
            "190 CM (6'23)", "191 CM (6'27)", "192 CM (6'30)", "193 CM (6'33)",
            "194 CM (6'36)", "195 CM (6'40)", "196 CM (6'43)"};
    String[] eyeColors = {"Select eye color", "Brown", "Hazel", "Blue", "Green", "Gray", "Amber"};
    String[] hairColors = {"Select hair color", "Black", "Brown", "Light", "Red"};
    String[] zodiacSign = {"Select zodiac sign", "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo", "Libra", "Scorpio",
            "Sagittarius", "Capricorn", "Aquarius", "Pisces"};
    String ACTION = "action";
    String MOBILE_NO = "mobile_no";
    String CARD_TYPE = "card_type";
    String PROFILE_IMG_CLIENT = Environment.getBaseUrl() + "uploads/document/";
    String PROFILE_IMG = Environment.getBaseUrl() + "uploads/document/" + RealmController.getUser().getId() + "/";
    String PROFILE_WORK = Environment.getBaseUrl() + "uploads/devices/" + RealmController.getUser().getId() + "/";
    String DEVICES_IMG = Environment.getBaseUrl() + "uploads/devices/";
    String CATEGORY_IMG = Environment.getBaseUrl() + "uploads/category/";
    String PARTNER_IMG = Environment.getBaseUrl() + "uploads/partners/";
    String NOTIFICATION_IMAGE_URL = Environment.getBaseUrl()+"uploads/notification/";
    String TERMANDPOLICYURL = Environment.getBaseUrl()+"api/static_page?type=";
    long ONE_HOUR_MILLISECONDS = 3600000;
    long FIFTEEN_DAYS_MILLISECONDS = 1296000000;
    int FILTER_MIN_PRICE = 0;
    int FILTER_MAX_PRICE = 500;
    String TECH = "tech";
    String NAME_UPDATE = "name_update";
    String AGE = "age";
    String IMAGE_URL = "image_url";
    String SPEAKS = "speaks";
    String ADDRESS = "address";
    String IMAGE = "image";
    String KEY = "key";
    String UNREADCOUNT = "unreadcount";
    String KEYWORD = "keyword";
    String TOGGLE = "toggle";
    String BODY = "body";
    String MESSAGE = "message";
    String BOOKING_ID = "booking_id";
    String BOOKING_DETAIL_ID = "booking_detail_id";
    String SERVICE_ID = "service_id";
    String DAYS = "days";
    String TIME = "time";
    String APPLY = "apply";
    String END_DATE = "end_date";
    String START_DATE = "start_date";
    String PRICE_MAX = "price_max";
    String PRICE_MIN = "price_min";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String DISTANCE_INS = "distance_ins";
    String LOCATION = "location";
    String COUNTRY = "country";
    String BOOKING_STATUS = "booking_status";
    String EXPLAIN = "explain";
    String EVENT_TYPE = "event_type";
    String EVENT = "event";
    String RECHECK_REASON = "recheck_reason";
    String REASON = "reason";
    String CUSTOMER_PHONE = "customer_phone";
    String CUSTOMER_CHAT = "customer_chat";
    String FORM_INFO = "form_info";
    String ATTEND_VISIT = "attend_visit";
    String CUSTOMER_NAME = "customer_name";
    String CUSTOMER_RELATION = "customer_relation";
    String SIGNATURE = "signature";
    String DEVICES = "devices";
    String STATUS = "status";
   // String CAMAROUNDP_ = "CamaroundP_";
  //  String CAMAROUNDP_ = "CamaroundPDev_";
    String JOB_ID = "job_id";
    String JOB_AMOUNT = "job_amount";
    String REQUEST_DATE = "request_date";
    String WORKING_DAYS = "working_days";
    String COMMENT = "comment";
    String MATERIAL_INCLUDE = "material_include";
    String BID_ID = "bid_id";
    String GROUP_ID = "group_id";
    String NOT_VISIBLE = "not_visible";
    String SERIAL_NO = "serial_no";
    String BRAND = "brand";
    String NOTIFICATION = "notification";
    String MODAL = "modal";
    String JOB_DEVICE_ID = "job_device_id";
    String JOB_DEVICES_ID = "job_devices_id";
    String CART_ID = "cart_id";
    String IMAGE_ID = "image_id";
    String OLD_FILE = "old_file";
    String JOB_DETAIL = "job_detail";
    String JOB_DETAIL_TYPE = "job_detail_type";
    String JOB_RESOLUTION = "job_resolution";
    String JOB_RESOLUTION_TYPE = "job_resolution_type";
    String TASK_PERFORMED = "task_performed";
    String OPERATING_SYSTEM = "operating_system";
    String OPERATING_SYSTEM_TYPE = "operating_system_type";
    String ADMIN_AMOUNT = "admin_amount";
    String TECH_AMOUNT = "tech_amount";
    String RECHECK_TECH_ID = "recheck_tech_id";
    String BROADCAST = "broadcast";
    String TECH_DISPUTE_COMPLETED = "tech_dispute_completed";
    String TECH_DECLINED = "tech_declined";
    String UPLOAD_PHOTOS = "upload_photos";
    String OLD_PASSWORD = "old_password";
    String NEW_PASSWORD = "new_password";
    String CONFIRM_PASSWORD = "confirm_password";
    String TECH_ID = "tech_id";
    String IDENTITY_TYPE = "identity_type";
    String SERVICE_NAME = "service_name";
    String BID_STATUS = "bid_status";
    String ADMIN_PERCENT = "admin_percent";
    String ADMIN_COMMISION = "admin_commision";
    String RATE_ONE = "rate_one";
    String RATE_TWO = "rate_two";
    String RATE_THREE = "rate_three";
    String VIEWREVIEW = "viewReview";
    String LIST = "list";
    String POSITION = "position";
    String USER_DISCOUNT = "user_discount";
    String ACTUAL_AMOUNT = "actual_amount";
    String ADMIN_COMMISION_DISCOUNT = "admin_commision_discount";
    //order status manage
    String[] orderStatus = {"inhold", "confirmed", "cancelled", "inprogress", "completed", "recheck_request", "dispute", "rejected", "declined"};
    String[] orderStatusTitle = {"In Hold", "Confirmed", "Cancelled", "In progress", "Completed", "Recheck Request", "Dispute", "Rejected", "Declined"};
    // int[] orderStatusColor = {R.color.greenBlue, R.color.greenBlue, R.color.salmon, R.color.darkSkyBlueTwo, R.color.greenBlue, R.color.greenBlue, R.color.tomato, R.color.tomato,R.color.tomato};


    //days manage
    String[] daysShow = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    String[] daysGet = {"\"Monday\"", "\"Tuesday\"", "\"Wednesday\"", "\"Thursday\"", "\"Friday\"", "\"Saturday\"", "\"Sunday\""};
    String[] daysGetShow = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    String[] bookingStatus = {"pending","open", "awarded", "cancelled", "completed", "inprogress", "dispute","incomplete","closed"};
    String[] bookingStatusTitle = {"Abierta","Abierta", "Adjudicada", "Cancelada", "Completada", "En curso", "En revisión","Incompleto","Cerrado"};
    int[] bookingStatusColor = {R.color.colorStatusOpen,R.color.colorStatusOpen, R.color.colorStatusAwarded, R.color.colorStatusCancelled, R.color.colorStatusCompleted, R.color.colorStatusInprogress, R.color.colorStatusEnRevisión,R.color.red,R.color.colorStatusCancelled};
}
