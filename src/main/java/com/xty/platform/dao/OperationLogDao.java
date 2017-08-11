package com.xty.platform.dao;

import com.xty.domain.FuzzyCondition;
import com.xty.platModel.basic.OperationLog;
import com.xty.platModel.basic.User;
import com.xty.platform.util.Util;
import com.xty.result.ResultData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eugene on 2015/8/21.
 */
@Repository
public class OperationLogDao extends BaseDao<OperationLog, Integer> {
    public ResultData fuzzy(User loginedUser, FuzzyCondition fuzzyCondition) {
        ResultData resultData = new ResultData();
        try {
            String hql = "FROM OperationLog WHERE 1 = 1 ";

            if (fuzzyCondition.getBeginDate() != null)
                hql += "AND d_date >'" + Util.DateFormatYMDHMS(fuzzyCondition.getBeginDate()) + "' ";
            if (fuzzyCondition.getEndDate() != null)
                hql += "AND d_date <'" + Util.DateFormatYMDHMS(fuzzyCondition.getEndDate()) + "' ";
            if(fuzzyCondition.getFuzzy() != null && fuzzyCondition.getFuzzy().trim().length() > 0)
                hql += "AND  s_userName like '%" + fuzzyCondition.getFuzzy() + "%' ";
            hql += "ORDER BY d_date DESC";

            int page = fuzzyCondition.getPage() == null ? 0 : fuzzyCondition.getPage();
            int limit = fuzzyCondition.getLimit() == null ? 0 : fuzzyCondition.getLimit();
            List<OperationLog> list = query(page, limit, hql);

            resultData.setData(list);
            Integer total = getTotalCount(hql);
            resultData.setTotalCount(total);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
        return resultData;
    }

    public List<OperationLog> query(OperationLog log, Integer start, Integer limit) {
        try {
            String hql = "FROM OperationLog WHERE 1 = 1 ";
            if (log.getS_user() != null && log.getS_user().trim().length() > 0)
                hql += "AND s_user = '" + log.getS_user() + "'";
            if (log.getBegin() != null && log.getEnd() != null)
                hql += " AND d_date BETWEEN '" + Util.DateFormatYMDHMS(log.getBegin())
                        + "' AND '" + Util.DateFormatYMDHMS(log.getEnd()) + "'";
            if (log.getS_type() != null && log.getS_type().trim().length() > 0) {
                hql += " AND  s_type LIKE '%" + log.getS_type() + "%'";
            }
            if (start == null)
                start = 1;
            if (limit == null)
                limit = 25;
            hql += " ORDER BY d_date DESC";
            return query(start, limit, hql);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
        return new ArrayList<>();
    }
}
