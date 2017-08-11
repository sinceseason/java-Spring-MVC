package com.xty.platform.service;

import com.xty.common.SystemServiceLog;
import com.xty.domain.FuzzyCondition;
import com.xty.platModel.basic.User;
import com.xty.platform.dao.UserDao;
import com.xty.platform.util.Definition;
import com.xty.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugene on 2015/8/18.
 */
@Service
public class UserService extends BaseService {
    @Autowired
    private UserDao userDao;

    /**
     * 系统登录
     * @param user User
     * @return User
     */
    public User login(User user){
        try{
            return userDao.login(user);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public User getByPK(String id){
        try{
            return userDao.getByPk(User.class, id);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return null;
    }

    public ResultData fuzzy(User loginedUser, FuzzyCondition fuzzyCondition){
        try{
            return userDao.fuzzy(loginedUser, fuzzyCondition);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return null;
    }

    public boolean changePassword(User user){
        try{
            return userDao.changePassword(user);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return false;
    }

    public boolean checkUsername(String username){
        try{
            return userDao.checkUsername(username);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return false;
    }

    /**
     * 保存系统用户
     * @param user User
     * @return boolean
     */
    @SystemServiceLog(s_module = Definition.LOG_USER_MODULE, s_type =Definition.LOG_CREATE_TYPE)
    public boolean saveUser(User user){
        try{
            return userDao.create(user) >= 0;
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return false;
    }

    /**
     * 更新系统用户
     * @param user User
     * @return boolean
     */
    @SystemServiceLog(s_module = Definition.LOG_USER_MODULE, s_type =Definition.LOG_MODIFY_TYPE)
    public boolean updateUser(User user){
        try{
            return userDao.update(user);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return false;
    }

    /**
     * 删除系统用户
     * @param user User
     * @return boolean
     */
    @SystemServiceLog(s_module = Definition.LOG_USER_MODULE, s_type =Definition.LOG_REMOVE_TYPE)
    public boolean removeUser(User user){
        try{
            return userDao.delete(user);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return false;
    }


    /**
     * 查询系统用户
     * @param username String
     * @return User
     */
    public User queryByName(String username){
        try{
            return userDao.queryByName(username);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex);
        }
        return null;
    }

}
