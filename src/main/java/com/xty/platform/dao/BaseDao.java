/**
 * BaseDao实现
 */
package com.xty.platform.dao;

import com.mysql.jdbc.Messages;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Shadow
 */
public class BaseDao<T, ID extends Serializable> implements IBaseDao<T, ID> {
    protected final Logger logger = Logger.getLogger(this.getClass());

    private String databeaseError = Messages
            .getString("BaseDao.databeaseError");

    @Autowired
    private SessionFactory sessionFactory;
    protected ApplicationContext applicationContext;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#batchDelete(java.lang.Class, long[])
     */
    public boolean batchDelete(Class clazz, long[] id) {
        boolean rs = false;
        try {
            String strId = "";
            for (int i = 0; i < id.length; i++) {
                if (i > 0)
                    strId += ", " + id[i];
                else
                    strId = "" + id[i];
            }

            for (int i = 0; i < id.length; i++) {
                this.delete(clazz, id[i]);
            }
            rs = true;
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.delete.batchDelete");
            logger.error("批量删除" + clazz.getName() + " 的实例到数据库 ,"
                    + error, e);
            e.printStackTrace();
        }
        return rs;
    }

    public void batchInsert(List<T> list){
        Session session = null;
        Transaction tr = null;
        boolean commitflag = false;
        Object recordId = null;

        try {
            session = this.getSession();
            tr = session.beginTransaction();
            for(T entity : list) {
                recordId = session.save(entity);
            }
            tr.commit();
            commitflag = true;
            session.flush();
            session.clear();
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.create.saveError");
            logger.error("批量保存 ,"
                    + error, e);
            e.printStackTrace();
        } finally {
            if (!commitflag) {
                try {
                    if (tr != null)
                        tr.rollback();
                } catch (Exception e1) {
                    logger.error("事务", e1);
                }
            }

            this.closeSession(session);
        }
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#create(java.lang.Object)
     */
    public Integer create(T entity) {
        Session session = null;
        Transaction tr = null;
        boolean commitflag = false;
        Object recordId = null;

        try {
            session = this.getSession();
            tr = session.beginTransaction();
            recordId = session.save(entity);
            tr.commit();
            commitflag = true;
            session.flush();
            session.clear();
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.create.saveError");
            logger.error("保存" + entity.getClass().getName() + " 的实例到数据库 ,"
                    + error, e);
            e.printStackTrace();
        } finally {
            if (!commitflag) {
                try {
                    if (tr != null)
                        tr.rollback();
                } catch (Exception e1) {
                    logger.error("事务", e1);
                }
            }

            this.closeSession(session);
            if (recordId == null)
                return 0;
            if(recordId instanceof String)
                return 0;
            return Integer.parseInt(String.valueOf(recordId));
        }
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#delete(String, List)
     */
    public boolean delete(String strhql, List params) {
        boolean rs = false;
        int result = 0;
        Session session = null;
        Transaction tr = null;
        boolean commitflag = false;

        try {
            session = this.getSession();
            tr = session.beginTransaction();
            Query query = session.createQuery(strhql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    query.setParameter(i, params.get(i));
                }
            }
            query.executeUpdate();
            tr.commit();
            commitflag = true;
            session.flush();
            rs = true;
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.getTotalCount.Error");
            logger.error(error, e);
            e.printStackTrace();
        } finally {
            this.closeSession(session);
        }
        return rs;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#delete(java.lang.Object)
     */
    public boolean delete(T entity) {
        boolean rs = false;
        Session session = null;
        Transaction tr = null;
        boolean commitflag = false;

        try {
            session = this.getSession();
            tr = session.getTransaction();
            tr.begin();
            session.delete(entity);
            tr.commit();
            commitflag = true;
            session.flush();
            session.clear();
            rs = true;
        } catch (Exception e) {
            if (tr != null)
                tr.rollback();
            String Error = Messages.getString("BaseDao.delete.Error");
            logger.error(Error + " Class=" + entity.getClass().getName(), e);
        } finally {
            if (!commitflag) {
                try {
                    if (tr != null)
                        tr.rollback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.closeSession(session);
        }
        return rs;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#delete(java.lang.Class, String)
     */
    public boolean delete(Class clazz, Object id) {
        boolean rs = false;
        try {
            T entity = null;
            if (id instanceof Integer)
                entity = this.getByPk(clazz, (Integer) id);
            else if (id instanceof Long)
                entity = this.getByPk(clazz, (Long) id);
            else if (id instanceof String)
                entity = this.getByPk(clazz, id.toString());
            if (entity != null)
                this.delete(entity);
            else
                logger.error(clazz.getName() + " 的关键字为 " + id + "  的对象不存在 ");
            rs = true;
        } catch (Exception e) {
            logger.error("  delete(Class, long)  excute is error  . Error="
                    + e.toString());
        }
        return rs;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#deleteAll(java.lang.Class)
     */
    public boolean deleteAll(Class clazz) {
        boolean rs = false;
        Session session = null;
        Transaction tr = null;
        boolean commitflag = false;

        try {
            session = this.getSession();
            tr = session.beginTransaction();
            Query query = session.createQuery(" delete   from "
                    + clazz.getName());
            query.executeUpdate();
            tr.commit();
            commitflag = true;
            session.flush();
            rs = true;
        } catch (Exception e) {
            String Error = Messages.getString("BaseDao.delete.Error");
            logger.error("从数据库中删除" + clazz.getName() + "的所有实例失败！", e);
        } finally {
            if (!commitflag) {
                try {
                    if (tr != null)
                        tr.rollback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.closeSession(session);
        }
        return rs;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#deleteAll(java.util.Collection)
     */
    public boolean deleteAll(Collection<T> entities) {
        boolean rs = false;
        Session session = null;
        Transaction tr = null;
        boolean commitflag = false;

        try {
            session = this.getSession();
            tr = session.beginTransaction();
            Iterator ite = entities.iterator();
            while (ite.hasNext())
                session.delete(ite.next());
            tr.commit();
            commitflag = true;
            session.flush();
            rs = true;
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.deleteAll.Error");
            logger.error(error, e);
        } finally {
            if (!commitflag) {
                try {
                    if (tr != null)
                        tr.rollback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.closeSession(session);
        }
        return rs;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#excuteSql(java.lang.String)
     */
    public boolean excuteSql(String strsql) {
        boolean rs = false;
        Session session = null;
        Transaction tr = null;
        boolean commitflag = false;

        try {
            session = this.getSession();
            tr = session.beginTransaction();
            SQLQuery query = session.createSQLQuery(strsql);
            query.executeUpdate();
            tr.commit();
            commitflag = true;
            session.flush();
            rs = true;
        } catch (Exception e) {
            String Error = Messages.getString("BaseDao.excuteSql.Error");
            logger.error(Error, e);
        } finally {
            if (!commitflag) {
                try {
                    if (tr != null)
                        tr.rollback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.closeSession(session);
        }
        return rs;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#find(java.lang.String)
     */
    public List<T> find(String strhql) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error("执行数据库中查询时失败,语句为：" + strhql, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#find(java.lang.String, java.lang.Object)
     */
    public List<T> find(String strhql, Object param) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            query.setProperties(param);
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#find(java.lang.String, java.util.List)
     */
    public List<T> find(String strhql, List param) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            if (param != null) {
                for (int i = 0; i < param.size(); i++) {
                    query.setParameter(i, param.get(i));
                }
            }
//			query.setParameter(0, param);
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#findByNamedParam(java.lang.String,
     * java.lang.String, java.lang.Object)
     */
    public List<T> findByNamedParam(String strhql, String name, Object param) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            query.setParameter(name, param);
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#findBySql(java.lang.String)
     */
    public List<T> findBySql(String strsql) {
        Session session = null;
        List<T> result = null;

        try {
            session = this.getSession();
            Query query = session.createSQLQuery(strsql);
            result = query.list();
        } catch (Exception e) {
            String Error = Messages.getString("BaseDao.exceuteSQL.Error");
            logger.error(Error, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#findBySql(java.lang.String)
     */
    public List<T> findBySql(Class clazz, String strsql) {
        Session session = null;
        List<T> result = null;

        try {
            session = this.getSession();
            Query query = session.createSQLQuery(strsql).setResultTransformer(Transformers.aliasToBean(clazz));
            result = (List<T>) query.list();
        } catch (Exception e) {
            String Error = Messages.getString("BaseDao.exceuteSQL.Error");
            logger.error(Error, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#findBySql(int, int, String, Class)
     */
    public List<T> findBySql(int pageNo, int pageSize, String strsql, Class clazz) {
        Session session = null;
        List<T> result = null;

        try {
            session = this.getSession();
            Query query = session.createSQLQuery(strsql).setResultTransformer(Transformers.aliasToBean(clazz));
            if (pageNo > 0 && pageSize > 0) {
                query.setFirstResult((pageNo - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
            result = (List<T>) query.list();
        } catch (Exception e) {
            String Error = Messages.getString("BaseDao.exceuteSQL.Error");
            logger.error(Error, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#findBySql(java.lang.String,
     * java.util.List)
     */
    public List<T> findBySql(Class clazz, String strsql, List params) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createSQLQuery(strsql).setResultTransformer(Transformers.aliasToBean(clazz));
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    query.setParameter(i, params.get(i));
                }
            }
            result = (List<T>) query.list();
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.findBySql.Error");
            logger.error(error, e);
            e.printStackTrace();
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getByPk(java.lang.Class, int)
     */
    public T getByPk(Class clazz, int id) {
        T result = null;
        Session session = null;

        try {
            session = this.getSession();
            result = (T) session.get(clazz, new Integer(id));
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return (T) result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getByPk(java.lang.Class, long)
     */
    public T getByPk(Class clazz, long id) {
        T result = null;
        Session session = null;

        try {
            session = this.getSession();
            result = (T) session.get(clazz, new Long(id));
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return (T) result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getByPk(java.lang.Class,
     * java.lang.String)
     */
    public T getByPk(Class clazz, String id) {
        T result = null;
        Session session = null;

        try {
            session = this.getSession();
            result = (T) session.get(clazz, id);
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return (T) result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getSession()
     */
    public Session getSession() {
        return sessionFactory.openSession();
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getTotalCount(java.lang.String)
     */
    public int getTotalCount(String strhql) {
        int result = 0;
        Session session = null;

        try {
            String strsql = this.getQueryTotalCountString(strhql);
            session = this.getSession();
            Query query = session.createQuery(strsql);
            result = ((Long) query.iterate().next()).intValue();
//			List list = query.list();
//			result = this.getNum(list);
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
    * @see com.tonmx.platform.dao.IBaseDao#getTotalCount(java.lang.String)
    */
    public int findMaxId(String strhql) {
        int result = 0;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            result = ((Long) query.iterate().next()).intValue();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getTotalCount(java.lang.String,
     * java.lang.Object)
     */
    public int getTotalCount(String strhql, Object obj) {
        int result = 0;
        Session session = null;

        try {
            String strsql = this.getQueryTotalCountString(strhql);
            logger.debug("strsql=" + strsql);
            session = this.getSession();
            Query query = session.createQuery(strsql);
            query.setProperties(obj);
//            result = ((Long) query.iterate().next()).intValue();
            List list = query.setProperties(obj).list();
            result = this.getNum(list);
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.getTotalCount.Error");
            logger.error(error, e);
            e.printStackTrace();
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getTotalCount(java.lang.String,
     * java.util.List)
     */
    public int getTotalCount(String strhql, List params) {
        int result = 0;
        Session session = null;

        try {
            String strquery = this.getQueryTotalCountString(strhql);
            session = this.getSession();
            Query query = session.createQuery(strquery);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    query.setParameter(i, params.get(i));
                }
            }
//            result = ((Long) query.iterate().next()).intValue();
            List list = query.list();
            result = this.getNum(list);
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.getTotalCount.Error");
            logger.error(error, e);
            e.printStackTrace();
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getTotalCountBySql(java.lang.String)
     */
    public int getTotalCountBySql(String strsql) {
        int result = 0;
        Session session = null;

        try {
            strsql = this.getQueryTotalCountString(strsql);
            session = this.getSession();
//            List<T> list = session.createSQLQuery(strsql).list();
//            result = (Integer)list.get(0);
            List list = session.createSQLQuery(strsql).list();
            result = this.getNum(list);
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#getTotalCountBySql(java.lang.String,
     * java.util.List)
     */
    public int getTotalCountBySql(String strsql, List params) {
        int result = 0;
        Session session = null;

        try {
            strsql = this.getQueryTotalCountString(strsql);
            session = this.getSession();
            SQLQuery query = session.createSQLQuery(strsql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    query.setParameter(i, params.get(i));
                }
            }

//            result = ((Long) query.iterate().next()).intValue();
            List list = query.list();
            result = this.getNum(list);
        } catch (Exception e) {
            String error = Messages.getString("BaseDao.getTotalCount.Error");
            logger.error(error, e);
            e.printStackTrace();
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#loadAll(java.lang.String)
     */
    public List<T> loadAll(String strhql) {
        return this.find(strhql);
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#loadByPk(java.lang.Class,
     * java.lang.String, java.lang.Object)
     */
    public T loadByPk(Class clazz, String keyName, Object keyValue) {
        T result = null;
        String query = "from " + clazz.getName() + "  where " + keyName + "=? ";
        Session session = null;

        try {
            session = this.getSession();
            result = (T) session.createCriteria(clazz)
                    .add(Restrictions.eq(keyName, keyValue)).list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return (T) result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#query(int, int, java.lang.String)
     */
    public List<T> query(int pageNo, int pageSize, String strhql) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            if (pageNo > 0 && pageSize > 0) {
                query.setFirstResult((pageNo - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#query(int, int, java.lang.String,
     * java.lang.Object)
     */
    public List<T> query(int pageNo, int pageSize, String strhql, Object obj) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            query.setProperties(obj);
            if (pageNo > 0 && pageSize > 0) {
                query.setFirstResult((pageNo - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#query(int, int, java.lang.String,
     * java.util.List)
     */
    public List<T> query(int pageNo, int pageSize, String strhql, List params) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            Query query = session.createQuery(strhql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    query.setParameter(i, params.get(i));
                }
            }
            if (pageNo > 0 && pageSize > 0) {
                query.setFirstResult((pageNo - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#queryBySql(int, int, java.lang.String)
     */
    public List<T> queryBySql(int pageNo, int pageSize, String strsql) {
        List<T> result = null;
        Session session = null;

        try {
            session = this.getSession();
            SQLQuery query = session.createSQLQuery(strsql);
            if (pageNo > 0 && pageSize > 0) {
                query.setFirstResult((pageNo - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#queryBySql(int, int, java.lang.String,
     * java.util.List)
     */
    public List<T> queryBySql(int pageNo, int pageSize, String strsql, List params) {
        List<T> result = null;
        Session session = null;
        try {
            session = this.getSession();
            SQLQuery query = session.createSQLQuery(strsql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    query.setParameter(i, params.get(i));
                }
            }
            if (pageNo > 0 && pageSize > 0) {
                query.setFirstResult((pageNo - 1) * pageSize);
                query.setMaxResults(pageSize);
            }
            result = (List<T>) query.list();
        } catch (Exception e) {
            logger.error(databeaseError, e);
        } finally {
            this.closeSession(session);
        }
        return result;
    }

    /*
     * @see com.tonmx.platform.dao.IBaseDao#update(java.lang.Object)
     */
    public boolean update(T entity) {
        boolean rs = false;
        Session session = null;
        Transaction tr = null;

        try {
            session = this.getSession();
            tr = session.beginTransaction();
            session.update(entity);
            tr.commit();
            session.flush();
            session.clear();
            rs = true;
        } catch (Exception e) {
            if (tr != null)
                tr.rollback();
            String Error = Messages.getString("BaseDao.update.Error");
            logger.error(Error, e);
        } finally {
            this.closeSession(session);
        }
        return rs;
    }

    /**
     * 功能：关闭session
     *
     * @param session
     */
    protected void closeSession(Session session) {
        if (session != null && session.isOpen())
            session.close();
        session = null;
    }

    /**
     * 功能：得到查询记录总数的语句（包含sql与hql）
     *
     * @param queryString
     * @return
     */
    private String getQueryTotalCountString(String queryString) {
        int form_index = queryString.toLowerCase().indexOf("from ");
        int orderby_index = queryString.indexOf(" order by ");
        if (form_index < 0) {
            return null;
        }
        String strsql = " select count(*) ";
        if (orderby_index > -1) {
            strsql = strsql + queryString.substring(form_index, orderby_index);
        } else {
            strsql = strsql + queryString.substring(form_index);
        }
        return strsql;
    }

    /**
     * 功能：得到记录数的方法
     *
     * @param list
     * @return
     */
    protected int getNum(List list) {
        int result = 0;
        if (list != null || list.size() > 0)
            result = Integer.parseInt(list.get(0).toString());
        return result;
    }

}