package me.exrates.model;

import lombok.Data;

/**
 * Created by maks on 31.03.2017.
 */

@Data
public class SessionParams {

    private int id;
    private int userId;
    private int sessionTimeSeconds;
    private int sessionLifeTypeId;

    public SessionParams() {
    }

    public SessionParams(int sessionTimeSeconds, int sessionLifeType) {
        this.sessionTimeSeconds = sessionTimeSeconds;
        this.sessionLifeTypeId = sessionLifeType;
    }
}
