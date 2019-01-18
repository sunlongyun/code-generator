package com.lianshang.generator.commons;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IService<DTO> {
    /**
     * 添加对象
     * @param dto
     * @return
     */
    public Boolean save(DTO dto);

    /**
     * 根据id修改对象
     * @param dto
     * @return
     */
    public Boolean update(DTO dto);

    /**
     * 批量更新
     * @param dto
     * @param example
     * @return
     */
    public Boolean batchUpdate(DTO dto, Serializable example);
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
    public DTO getById(Serializable id);

    /**
     * 0.0.2版本该方法已废除,不建议继续使用
     * 根据id查询对象
     * @param id
     * @return
     */
    @Deprecated
    public DTO getById(Serializable id, String dtoClassName);
    /**
     * 根据id列表查询对象列表
     * @param idList
     * @return
     */
    public List<DTO> getListByIds(Collection<? extends Serializable> idList);

    /**
     * 0.0.2版本该方法已废除,不建议继续使用
     * 根据id列表查询对象列表
     * @param idList
     * @return
     */
    @Deprecated
    public List<DTO> getListByIds(Collection<? extends Serializable> idList, String dtoClassName);

    /**
     * 根据example查询
     * @param example
     * @return
     */
    public List<DTO> getList(Serializable example);

    /**
     * 0.0.2版本该方法已废除,不建议继续使用
     * 根据example查询
     * @param example
     * @return
     */
    @Deprecated
    public List<DTO> getList(Serializable example, String dtoClassName);

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
