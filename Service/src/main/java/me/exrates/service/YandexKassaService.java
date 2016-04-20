package me.exrates.service;

import me.exrates.model.CreditsOperation;
import org.springframework.web.servlet.view.RedirectView;

public interface YandexKassaService {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

}
