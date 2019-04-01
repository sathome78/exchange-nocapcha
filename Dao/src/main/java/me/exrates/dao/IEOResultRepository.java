package me.exrates.dao;

import me.exrates.model.IEOResult;

public interface IEOResultRepository {

    IEOResult create(IEOResult ieoResult);

    boolean updateStatus(int id, IEOResult.IEOResultStatus status);
}
