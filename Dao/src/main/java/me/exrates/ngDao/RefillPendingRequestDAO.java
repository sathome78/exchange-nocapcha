package me.exrates.ngDao;

import me.exrates.model.ngModel.RefillPendingRequestDto;

import java.util.List;

public interface RefillPendingRequestDAO {

    List<RefillPendingRequestDto> getPendingRefillRequests(long userId, List<Integer> withdrawRequestStatuses,
                                                           List<Integer> refillRequestStatuses,
                                                           List<Integer> transferStatuses);
}
