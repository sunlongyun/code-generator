package com.lianshang.generator.commons;

import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IService<N> {
    /**
     * 添加对象
     * @param n
     * @return
     */
    public boolean save(N n);

    /**
     * 根据id修改对象
     * @param id
     * @return
     */
    public boolean updateById(Serializable id);

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
     * 根据map查询对象列表
     * @param map
     * @return
     */
    public List<N> getListByMap(Map<String, Object> map);

    /**
     * 根据map查询总数量
     * @param map
     * @return
     */
    public int getCount(Map<String, Object> map);

    /**
     * 根据map分页
     * @param pageNo
     * @param pageSize
     * @param map
     * @return
     */
    public PageInfo<N> getPageInfo(int pageNo, int pageSize, Map<String, Object> map);
}
