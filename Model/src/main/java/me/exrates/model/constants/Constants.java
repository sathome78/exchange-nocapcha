package me.exrates.model.constants;

public interface Constants {
    interface ErrorApi {

        int USER_WRONG_CURRENT_PASSWORD = 1010;
        int USER_INCORRECT_PASSWORDS = 1011;

        int QUBERA_PARAMS_OVER_LIMIT = 1200;
        int QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR = 1201;
        int QUBERA_SAVE_ACCOUNT_RESPONSE_ERROR = 1202;
        int QUBERA_RESPONSE_CREATE_APPLICANT_ERROR = 1203;
        int QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR = 1204;
        int QUBERA_ACCOUNT_NOT_FOUND_ERROR = 1205;
        int QUBERA_ACCOUNT_RESPONSE_ERROR = 1206;
        int QUBERA_PAYMENT_TO_MASTER_ERROR = 1207;
        int QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR = 1208;
        int QUBERA_NOT_ENOUGH_MONEY_FOR_PAYMENT = 1209;
        int QUBERA_ERROR_RESPONSE_CREATE_EXTERNAL_PAYMENT = 1210;
        int QUBERA_KYC_ERROR_GET_STATUS = 1211;
        int QUBERA_KYC_RESPONSE_ERROR_GET_STATUS = 1212;
    }
}
