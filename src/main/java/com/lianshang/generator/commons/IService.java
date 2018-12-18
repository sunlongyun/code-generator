package com.lianshang.generator.commons;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IService<N> {
    /**
     * 添加对象
     * @param n
     * @return
     */
    public Boolean save(N n);

    /**
     * 根据id修改对象
     * @param n
     * @return
     */
    public Boolean update(N n);

    /**
     * 批量更新
     * @param n
     * @param example
     * @return
     */
    public Boolean batchUpdate(N n, Serializable example);
    /**
     * 根据id删除对象(逻辑删除)
     * @param id
     * @return
     */
    public Boolean deleteById(Serializable id);

    /**
     * 根据id查询对象
     * @param id
     * @return
     */
    public N getById(Serializable id);

    /**
     * 0.0.2版本该方法已废除,不建议继续使用
     * 根据id查询对象
     * @param id
     * @return
     */
    @Deprecated
    public N getById(Serializable id, String dtoClassName);
    /**
     * 根据id列表查询对象列表
     * @param idList
     * @return
     */
    public List<N> getListByIds(Collection<? extends Serializable> idList);

    /**
     * 0.0.2版本该方法已废除,不建议继续使用
     * 根据id列表查询对象列表
     * @param idList
     * @return
     */
    @Deprecated
    public List<N> getListByIds(Collection<? extends Serializable> idList, String dtoClassName);

    /**
     * 根据example查询
     * @param example
     * @return
     */
    public List<N> getList(Serializable example);

    /**
     * 0.0.2版本该方法已废除,不建议继续使用
     * 根据example查询
     * @param example
     * @return
     */
    @Deprecated
    public List<N> getList(Serializable example, String dtoClassName);

    /**
     * 根据example查询总数量
     * @param example
     * @return
     */
    public int getCount(Serializable example);

    /**
     * 根据map分页
     * @param pageNo
     * @param pageSize
     * @param example
     * @return
     */
    public PageInfo getPageInfo(Integer pageNo, Integer pageSize, Serializable example);

}
