package me.exrates.model.constants;

public interface ErrorApiTitles {

    String USER_WRONG_CURRENT_PASSWORD = "USER_WRONG_CURRENT_PASSWORD";
    String USER_INCORRECT_PASSWORDS = "USER_INCORRECT_PASSWORDS";
    String USER_EMAIL_NOT_FOUND = "USER_EMAIL_NOT_FOUND";
    String USER_CREDENTIALS_NOT_COMPLETE = "USER_CREDENTIALS_NOT_COMPLETE";
    String EMAIL_AUTHORIZATION_FAILED = "EMAIL_AUTHORIZATION_FAILED";
    String USER_REGISTRATION_NOT_COMPLETED = "USER_REGISTRATION_NOT_COMPLETED";
    String USER_NOT_ACTIVE = "USER_NOT_ACTIVE";
    String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    String FAILED_TO_GET_USER_TOKEN = "FAILED_TO_GET_USER_TOKEN";
    String GOOGLE_AUTHORIZATION_FAILED = "GOOGLE_AUTHORIZATION_FAILED";
    String GOOGLE2FA_SUBMIT_FAILED = "GOOGLE2FA_SUBMIT_FAILED";
    String VERIFY_GOOGLE2FA_FAILED = "VERIFY_GOOGLE2FA_FAILED";
    String GOOGLE2FA_DISABLE_FAILED = "GOOGLE2FA_DISABLE_FAILED";
    // pin code sent over email needed to proceed
    String REQUIRED_EMAIL_AUTHORIZATION_CODE = "REQUIRED_EMAIL_AUTHORIZATION_CODE";
    String FAILED_TO_REGISTER_USER = "FAILED_TO_REGISTER_USER";
    // Google or email
    String REQUIRED_MODE_AUTHORIZATION_CODE = "REQUIRED_%s_AUTHORIZATION_CODE";

    String QUBERA_PARAMS_OVER_LIMIT = "QUBERA_PARAMS_OVER_LIMIT";
    String QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR = "QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR";
    String QUBERA_SAVE_ACCOUNT_RESPONSE_ERROR = "QUBERA_SAVE_ACCOUNT_RESPONSE_ERROR";
    String QUBERA_RESPONSE_CREATE_APPLICANT_ERROR = "QUBERA_RESPONSE_CREATE_APPLICANT_ERROR";
    String QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR = "QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR";
    String QUBERA_ACCOUNT_NOT_FOUND_ERROR = "QUBERA_ACCOUNT_NOT_FOUND_ERROR";
    String QUBERA_ACCOUNT_RESPONSE_ERROR = "QUBERA_ACCOUNT_RESPONSE_ERROR";
    String QUBERA_PAYMENT_TO_MASTER_ERROR = "QUBERA_PAYMENT_TO_MASTER_ERROR";
    String QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR = "QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR";
    String QUBERA_NOT_ENOUGH_MONEY_FOR_PAYMENT = "QUBERA_NOT_ENOUGH_MONEY_FOR_PAYMENT";
    String QUBERA_ERROR_RESPONSE_CREATE_EXTERNAL_PAYMENT = "QUBERA_ERROR_RESPONSE_CREATE_EXTERNAL_PAYMENT";
    String QUBERA_KYC_ERROR_GET_STATUS = "QUBERA_KYC_ERROR_GET_STATUS";
    String QUBERA_KYC_RESPONSE_ERROR_GET_STATUS = "QUBERA_KYC_RESPONSE_ERROR_GET_STATUS";

    String EMPTY_CHAT_MESSAGE = "EMPTY_CHAT_MESSAGE";
    String FAIL_TO_PERSIST_CHAT_MESSAGE = "FAIL_TO_PERSIST_CHAT_MESSAGE";
    String FAIL_TO_GET_CURRENCY_PAIR_INFO = "FAIL_TO_GET_CURRENCY_PAIR_INFO";
    String FAILED_ACCEPT_TRANSFER = "FAILED_ACCEPT_TRANSFER";
    String FAILED_TO_SEND_USER_EMAIL = "FAILED_TO_SEND_USER_EMAIL";
    String CREATE_ORDER_FAILED = "CREATE_ORDER_FAILED";
    String DELETE_ORDER_FAILED = "DELETE_ORDER_FAILED";
    String FAILED_TO_GET_BALANCE_BY_CURRENCY = "FAILED_TO_GET_BALANCE_BY_CURRENCY";
    String FAILED_TO_FILTERED_ORDERS = "FAILED_TO_FILTERED_ORDERS";
    String FAILED_TO_GET_LAST_ORDERS = "FAILED_TO_GET_LAST_ORDERS";
    String FAILED_TO_SEND_RECOVERY_PASSWORD = "FAILED_TO_SEND_RECOVERY_PASSWORD";
    String FAILED_TO_CREATE_RECOVERY_PASSWORD = "FAILED_TO_CREATE_RECOVERY_PASSWORD";

    String PREFERRED_LOCALE_NOT_SAVE = "PREFERRED_LOCALE_NOT_SAVE";
    String UPDATE_SESSION_PERIOD_FAILED = "UPDATE_SESSION_PERIOD_FAILED";
    String UPDATE_USER_NOTIFICATION_FAILED = "UPDATE_USER_NOTIFICATION_FAILED";
    String UPLOAD_USER_VERIFICATION_FAILED = "UPLOAD_USER_VERIFICATION_FAILED";
    String UPLOAD_USER_VERIFICATION_DOCS_FAILED = "UPLOAD_USER_VERIFICATION_DOCS_FAILED";
    String FAILED_MANAGE_USER_FAVORITE_CURRENCY_PAIRS = "FAILED_MANAGE_USER_FAVORITE_CURRENCY_PAIRS";
}
        String PREFERRED_LOCALE_NOT_SAVE = "PREFERRED_LOCALE_NOT_SAVE";
        String FAILED_TO_CREATE_WITHDRAW_REQUEST = "FAILED_TO_CREATE_WITHDRAW_REQUEST";
        String FAILED_OUTPUT_CREDITS = "FAILED_OUTPUT_CREDITS";
        String FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL = "FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL";
}