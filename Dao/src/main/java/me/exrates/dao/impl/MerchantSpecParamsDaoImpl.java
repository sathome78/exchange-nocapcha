package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maks on 09.06.2017.
 */

@Log4j2
@Repository
public class MerchantSpecParamsDaoImpl implements MerchantSpecParamsDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public MerchantSpecParamDto getByMerchantIdAndParamName(int merchantId, String paramName) {
        String sql = " SELECT * FROM MERCHANT_SPEC_PARAMETERS WHERE merchant_id = :merchant_id AND param_name = :param_name ";
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("param_name", paramName);
        return jdbcTemplate.queryForObject(sql, params, new RowMapper<MerchantSpecParamDto>() {
            @Override
            public MerchantSpecParamDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                MerchantSpecParamDto dto = new MerchantSpecParamDto();
                dto.setId(rs.getInt("id"));
                dto.setMerchantId(rs.getInt("merchant_id"));
                dto.setParamName(rs.getString("param_name"));
                dto.setParamValue(rs.getString("param_value"));
                return dto;
            }
        });
    }
}
