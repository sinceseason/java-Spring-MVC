package com.xty.platform.service;

import com.xty.domain.FuzzyCondition;
import com.xty.platModel.basic.OperationLog;
import com.xty.platModel.basic.User;
import com.xty.platform.dao.OperationLogDao;
import com.xty.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eugene on 2015/8/18.
 */
@Service
public class LogService extends BaseService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private OperationLogDao operationLogDao;

    public ResultData fuzzy(User loginedUser, FuzzyCondition fuzzyCondition){
        try{
            return operationLogDao.fuzzy(loginedUser, fuzzyCondition);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return null;
    }

    public boolean save(OperationLog operationLog){
        try{
            User loginedUser = (User) request.getSession().getAttribute(request.getHeader("token"));
            if(loginedUser!=null) {
                operationLog.setS_user(loginedUser.getId().toString());
                operationLog.setS_userName(loginedUser.getFullname());
                operationLog.setS_ip(request.getRemoteAddr());
                operationLog.setD_date(new Date());
                return operationLogDao.create(operationLog) > 0;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return false;
    }

    public List<OperationLog> query(OperationLog log, Integer start, Integer limit){
        try{
            return operationLogDao.query(log, start, limit);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return new ArrayList<>();
    }
}
