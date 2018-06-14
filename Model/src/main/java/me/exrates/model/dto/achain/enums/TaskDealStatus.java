package me.exrates.model.dto.achain.enums;

/**
 * Created by Maks on 14.06.2018.
 */
public enum TaskDealStatus {

    TASK_INI(0, "Block initial save"),

    TASK_TRX_CREATE(1, "Block initial save"),

    TASK_CALCULATE_USER_INFO(2, "The user balance is calculated"),

    TASK_SUBMIT_SUCCESS(3, "Successful task submission"),

    TASK_SUBMIT_FAIL(4, "Failed to mention the task"),

    TASK_SUCCESS(5, "Task completed successfully"),;

    private final int key;

    private final String desc;

    TaskDealStatus(int key) {
        this.key = key;
        this.desc = "";
    }

    TaskDealStatus(int key, String desc) {
        this.key = key;
        this.desc = desc;
    }


    public int getIntKey() {
        return key;
    }


    public String getDesc() {
        return desc;
    }
}
