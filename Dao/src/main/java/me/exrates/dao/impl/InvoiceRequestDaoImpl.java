package me.exrates.dao.impl;

import me.exrates.dao.InvoiceRequestDao;
import me.exrates.model.InvoiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Created by ogolv on 26.07.2016.
 */
public class InvoiceRequestDaoImpl implements InvoiceRequestDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final static RowMapper<InvoiceRequest> invoiceRequestRowMapper = (resultSet, i) -> {
        final InvoiceRequest invoiceRequest = new InvoiceRequest();

        return invoiceRequest;
    };

    private static final String SELECT_ALL = "";


    @Override
    public void create(InvoiceRequest invoiceRequest) {
        final String sql = "";

    }

    @Override
    public void delete(InvoiceRequest invoiceRequest) {

    }

    @Override
    public void update(InvoiceRequest invoiceRequest) {

    }

    @Override
    public Optional<InvoiceRequest> findById(int id) {
        return null;
    }

    @Override
    public List<InvoiceRequest> findAll() {
        return null;
    }
}
