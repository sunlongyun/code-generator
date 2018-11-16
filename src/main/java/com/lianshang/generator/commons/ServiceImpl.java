package com.lianshang.generator.commons;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @AUTHOR 孙龙云
 * @date 2018-11-14 下午6:10
 */
public class ServiceImpl<M extends LsBaseMapper<T>, T, DTO> implements IService<DTO> {
    @Autowired
    protected M baseMapper;

    /**
     * entity 对象转dto对象
     * @param entity
     * @return
     */
    private DTO entityToDto(T entity) {
        try {
            String xClassName = entity.getClass().getName();
            String yClassName = xClassName.replaceAll("entity\\.","dto\\.")+"Dto";
            Object target = Class.forName(yClassName).newInstance();
            BeanUtils.copyProperties(entity, target);
            return (DTO) target;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * dto 对象转entity对象
     *
     * @param dto
     * @return
     */
    private T dtoToEntity(Object dto) {
        try {
            String xClassName = dto.getClass().getName();
            String yClassName = xClassName
              .replaceAll("dto\\.", "entity\\.").replaceAll("Dto$", "");
            Object target = Class.forName(yClassName).newInstance();
            BeanUtils.copyProperties(dto, target);
            return (T) target;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * 添加对象
     *
     * @param n
     * @return
     */
    @Override
    public boolean save(DTO n) {
        T target = dtoToEntity(n);
        int r = baseMapper.insert(target);
        return r > 0;
    }

    /**
     * 根据id修改对象
     *
     * @param n
     * @return
     */
    @Override
    public boolean update(DTO n) {
        T target = dtoToEntity(n);
        int r = baseMapper.updateById(target);
        return r > 0;
    }

    /**
     * 根据id删除对象(逻辑删除)
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(Serializable id) {
        int r = baseMapper.deleteById(id);
        return r >0;
    }

    /**
     * 根据id查询对象
     *
     * @param id
     * @return
     */
    @Override
    public DTO getById(Serializable id) {
        T t = baseMapper.selectById(id);
        DTO n = entityToDto(t);
        return n;
    }

    /**
     * 根据id列表查询对象列表
     *
     * @param idList
     * @return
     */
    @Override
    public List<DTO> getListByIds(Collection<? extends Serializable> idList) {
        List<T> list = baseMapper.selectBatchIds(idList);
        List<DTO> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for (T t : list) {
                DTO n = entityToDto(t);
                resultList.add(n);
            }
        }
        return resultList;
    }

    /**
     * 根据map查询对象列表
     *
     * @param map
     * @return
     */
    @Override
    public List<DTO> getListByColumnMap(Map<String, Object> map) {
        List<T> list = baseMapper.selectByMap(map);
        List<DTO> resultList = new ArrayList<>();
        for (T t : list) {
            DTO dto = entityToDto(t);
            resultList.add(dto);
        }
        return resultList;
    }

    /**
     * 根据map查询总数量
     *
     * @param map
     * @return
     */
    @Override
    public int getCountByColumnMap(Map<String, Object> map) {
        int count = 0;
        List<T> list = baseMapper.selectByMap(map);
        if (null != list) {
            count = list.size();
        }
        return count;
    }

    /**
     * 根据map分页
     *
     * @param pageNo
     * @param pageSize
     * @param map
     * @return
     */
    @Override
    public PageInfo getPageInfoByColumnMap(int pageNo, int pageSize, Map<String, Object> map) {
        if (pageNo <= 0) pageNo = 1;
        if (pageSize <= 0) pageSize = 10;
        PageHelper.startPage(pageNo, pageSize);
        List<DTO> dtoList = getListByColumnMap(map);
        PageInfo pageInfo = PageInfo.getPageInfo(dtoList);
        return pageInfo;
    }

    /**
     * 根据example查询
     *
     * @param example
     * @return
     */
    @Override
    public List<DTO> getList(Serializable example) {
        List<T> list = baseMapper.selectByExample(example);
        List<DTO> resultList = new ArrayList<>();
        if (null != list) {
            for (T t : list) {
                DTO dto = entityToDto(t);
                resultList.add(dto);
            }
        }
        return resultList;
    }

    /**
     * 根据example查询总数量
     *
     * @param example
     * @return
     */
    @Override
    public int getCount(Serializable example) {
        int count = 0;
        List<DTO> list = getList(example);
        if (null != list) {
            count = list.size();
        }
        return count;
    }

    /**
     * 根据example分页
     *
     * @param pageNo
     * @param pageSize
     * @param example
     * @return
     */
    @Override
    public PageInfo getPageInfo(int pageNo, int pageSize, Serializable example) {
        if (pageNo <= 0) pageNo = 1;
        if (pageSize <= 0) pageSize = 10;
        PageHelper.startPage(pageNo, pageSize);
        List<DTO> list = getList(example);
        PageInfo pageInfo = PageInfo.getPageInfo(list);
        return pageInfo;
    }
}
