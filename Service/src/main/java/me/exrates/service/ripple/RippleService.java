package me.exrates.service.ripple;

import me.exrates.model.CreditsOperation;

/**
 * Created by maks on 11.05.2017.
 */
public interface RippleService {
    String createAddress(CreditsOperation creditsOperation);
}
