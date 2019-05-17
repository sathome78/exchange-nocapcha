package me.exrates.ngService;


import me.exrates.model.ngModel.RefillPendingRequestDto;

import java.util.List;

public interface RefillPendingRequestService {
    List<RefillPendingRequestDto> getPendingRefillRequests(long userId);
}
