package me.exrates.model.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.util.ClassLoaderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maks on 13.04.2017.
 */
@Data
public class RefFilterData {

    private String email;
    private String dateFrom;
    private String dateTo;
    private List<Integer> currencyIds;

    public Map<String, Object> getSQLParamsMap() {
        StringBuilder sqlWhere = new StringBuilder();
        Map<String, Object> paramsMap = new HashMap<>();
        if (!StringUtils.isEmpty(dateFrom)) {
            paramsMap.put("dateFrom", dateFrom);
            sqlWhere.append(" AND (TR.datetime BETWEEN STR_TO_DATE(:dateFrom, '%Y-%m-%d %H:%i:%s') " +
                    "AND STR_TO_DATE(:dateTo, '%Y-%m-%d %H:%i:%s'))) ");
        }
        if (!StringUtils.isEmpty(dateTo)) {
            paramsMap.put("dateTo", dateTo);
        }
        if (currencyIds != null) {
            paramsMap.put("currencyIds", currencyIds);
            sqlWhere.append(" AND (TX.currency_id IN (:currencyIds)) ");
        }
        return paramsMap;
    }
}
