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
    public boolean save(N n);

    /**
     * 根据id修改对象
     * @param n
     * @return
     */
    public boolean update(N n);

    /**
     * 根据id删除对象(逻辑删除)
     * @param id
     * @return
     */
    public boolean deleteById(Serializable id);
    /**
     * 根据id查询对象
     * @param id
     * @return
     */
    public N getById(Serializable id);
    /**
     * 根据id列表查询对象列表
     * @param idList
     * @return
     */
    public List<N> getListByIds(Collection<? extends Serializable> idList);

    /**
     * 根据example查询
     * @param example
     * @return
     */
    public List<N> getList(Serializable example);

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
    public PageInfo getPageInfo(int pageNo, int pageSize, Serializable example);
}
