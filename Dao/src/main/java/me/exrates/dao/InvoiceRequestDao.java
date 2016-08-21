package me.exrates.dao;

import me.exrates.model.InvoiceRequest;
import me.exrates.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by ogolv on 26.07.2016.
 */
public interface InvoiceRequestDao {

    void create(InvoiceRequest invoiceRequest, User user);

    void delete(InvoiceRequest invoiceRequest);

    void setAcceptance(InvoiceRequest invoiceRequest);

    Optional<InvoiceRequest> findById(int id);

    List<InvoiceRequest> findAll();
}
