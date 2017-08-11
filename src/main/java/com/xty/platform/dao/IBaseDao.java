/** * BaseDao Interface */package com.xty.platform.dao;import org.hibernate.Session;import java.io.Serializable;import java.util.Collection;import java.util.List;/** * @author Shadow */public interface IBaseDao<T, ID extends Serializable> {    /**     * 功能：获得数据库连接的Session     *     * @return Session     */    public Session getSession();    /**     * 功能：根据hql语句得到记录总数     *     * @param strhql     * @return int     */    public int getTotalCount(String strhql);    /**     * 功能：根据sql语句得到记录总数     *     * @param strsql     * @return int     */    public int getTotalCountBySql(String strsql);    /**     * 功能：根据hql语句得到记录总数     *     * @param strhql     * @param obj     * @return int     */    public int getTotalCount(String strhql, Object obj);    /**     * 功能：根据hql语句得到记录总数     *     * @param strhql     * @param params     * @return int     */    public int getTotalCount(String strhql, List params);    /**     * 功能：根据sql语句得到记录总数     *     * @param strsql     * @param params     * @return     */    public int getTotalCountBySql(String strsql, List params);    public void batchInsert(List<T> list);    /**     * 功能：增加记录     *     * @param entity     */    public Integer create(T entity);    /**     * 功能：修改记录     *     * @param entity     */    public boolean update(T entity);    /**     * 功能：删除记录     *     * @param strhql     * @param params     */    public boolean delete(String strhql, List params);    /**     * 功能：删除记录     *     * @param entity     */    public boolean delete(T entity);    /**     * 功能：删除数据     *     * @param clazz     * @param id     */    public boolean delete(Class clazz, Object id);    /**     * 功能：批量删除数据     *     * @param clazz     * @param id     */    public boolean batchDelete(Class clazz, long[] id);    /**     * 功能：删除表中的所有的记录     *     * @param clazz     */    public boolean deleteAll(Class clazz);    /**     * 功能：删除记录集中的所有的记录     *     * @param entities     */    public boolean deleteAll(Collection<T> entities);    /**     * 功能：通过主键查询记录     *     * @param clazz     * @param id     * @return Object     */    public T getByPk(Class clazz, int id);    /**     * 功能：通过主键查询记录     *     * @param clazz     * @param id     * @return Object     */    public T getByPk(Class clazz, long id);    /**     * 功能：通过主键查询记录     *     * @param clazz     * @param id     * @return Object     */    public T getByPk(Class clazz, String id);    /**     * 功能：通过关键字和值来进行查询     *     * @param clazz     * @param keyName     * @param keyValue     * @return 得到的Object是List     */    public T loadByPk(Class clazz, String keyName, Object keyValue);    /**     * 功能：根据hql查询记录     *     * @param strhql     * @return List     */    public List<T> find(String strhql);    /**     * 功能：根据hql查询记录     *     * @param strhql     * @param param     * @return List     */    public List<T> find(String strhql, Object param);    /**     * 功能：根据hql查询记录     *     * @param strhql     * @param name     * @param param     * @return List     */    public List<T> findByNamedParam(String strhql, String name, Object param);    /**     * 功能：SQL查询     *     * @param strsql     * @return     */    public List<T> findBySql(String strsql);    /**     * 功能：SQL查询     *     * @param strsql     * @return     */    public List<T> findBySql(Class clazz, String strsql);    /**     * 功能：SQL分页查询     *     * @param pageNo     * @param pageSize     * @param strsql     * @return     */    public List<T> findBySql(int pageNo, int pageSize, String strsql, Class clazz);    /**     * 功能：查询符合条件的记录。     *     * @param strsql     * @param params     * @return     */    public List<T> findBySql(Class clazz, String strsql, List params);    /**     * 功能：分页查询     *     * @param pageNo     * @param pageSize     * @param strhql     * @return List     */    public List<T> query(int pageNo, int pageSize, String strhql);    /**     * 功能：分页查询     *     * @param pageNo     * @param pageSize     * @param strhql     * @param obj     * @return List     */    public List<T> query(int pageNo, int pageSize, String strhql, Object obj);    /**     * 功能：分页查询     *     * @param pageNo     * @param pageSize     * @param strhql     * @param params     * @return     */    public List<T> query(int pageNo, int pageSize, String strhql, List params);    /**     * 功能：分页查询     *     * @param pageNo     * @param pageSize     * @param strsql     * @return List     */    public List<T> queryBySql(int pageNo, int pageSize, String strsql);    /**     * 功能：分页查询     *     * @param pageNo     * @param pageSize     * @param strsql     * @param params     * @return     */    public List<T> queryBySql(int pageNo, int pageSize, String strsql, List params);    /**     * 功能：执行SQL语句，主要是更新与删除记录的SQL语句     *     * @param strsql     */    public boolean excuteSql(String strsql);    public int findMaxId(String strhql);}