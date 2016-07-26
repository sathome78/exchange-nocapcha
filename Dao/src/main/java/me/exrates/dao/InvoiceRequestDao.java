package me.exrates.dao;

import me.exrates.model.InvoiceRequest;

import java.util.List;
import java.util.Optional;

/**
 * Created by ogolv on 26.07.2016.
 */
public interface InvoiceRequestDao {

    void create(InvoiceRequest invoiceRequest);

    void delete(InvoiceRequest invoiceRequest);

    void update(InvoiceRequest invoiceRequest);

    Optional<InvoiceRequest> findById(int id);

    List<InvoiceRequest> findAll();
}
