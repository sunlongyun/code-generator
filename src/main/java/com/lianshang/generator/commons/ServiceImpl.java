package com.lianshang.generator.commons;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 描述:
 *
 * @AUTHOR 孙龙云
 * @date 2018-11-14 下午6:10
 */
@Slf4j
public class ServiceImpl<M extends LsBaseMapper<T>, T, DTO> implements IService<DTO> {
    @Autowired
    protected M baseMapper;

    /**
     * entity 对象转dto对象
     * @param entity
     * @return
     */
    public DTO entityToDto(T entity) {
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
    public T dtoToEntity(Object dto) {
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
     * 拷贝列表
     */
    public <Y> List<Y> copyList(List<?> sourceList, Class<Y> yClass) {
        List<Y> list = new ArrayList<>();
        if (null != sourceList) {
            try {
                for (Object source : sourceList) {
                    Y y = yClass.newInstance();
                    BeanUtils.copyProperties(source, y);
                    list.add(y);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 拷贝对象
     */
    public <Y> Y copyObj(Object source, Class<Y> yClass) {
        try {
            Y y = yClass.newInstance();
            if(null != source){
                BeanUtils.copyProperties(source, y);
            }
            return y;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * 添加对象
     *
     * @param n
     * @return
     */
    @Override
    public Boolean save(DTO n) {
        T target = dtoToEntity(n);
        int r = baseMapper.insert(target);
        try {
            //id 拷贝
            Field entityId = target.getClass().getDeclaredField("id");
            Field dtoId = n.getClass().getDeclaredField("id");
            entityId.setAccessible(true);
            dtoId.setAccessible(true);
            Object id = entityId.get(target);//value
            dtoId.set(n, id);

            entityId.setAccessible(false);
            dtoId.setAccessible(false);
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("id拷贝异常:", ex);
        }
        return r > 0;
    }

    /**
     * 根据id修改对象
     *
     * @param n
     * @return
     */
    @Override
    @Transactional
    public Boolean update(DTO n) {
        T target = dtoToEntity(n);
        int r = baseMapper.updateById(target);
        return r > 0;
    }

    /**
     * 批量更新
     *
     * @param n
     * @param example
     * @return
     */
    public Boolean batchUpdate(DTO n, Serializable example) {
        try {
            if(null == example){
                throw new RuntimeException("批量更新条件不能为空");
            }
            List<DTO> dtoList = getList(example);
            if(null != dtoList){
                for(DTO dto : dtoList){
                    Field filedId = n.getClass().getDeclaredField("id");
                    filedId.setAccessible(true);
                    Object id = filedId.get(dto);
                    filedId.set(n, id);

                    filedId.setAccessible(false);

                    update(n);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    /**
     * 根据id删除对象(逻辑删除)
     *
     * @param id
     * @return
     */
    @Override
    public Boolean deleteById(Serializable id) {
        T t = baseMapper.selectById(id);
        try {
            Field f = t.getClass().getDeclaredField("validity");

            if (null == f) throw new RuntimeException("未定义[validity]逻辑删除字段");

            f.setAccessible(true);
            f.set(t, false);
            f.setAccessible(false);

        } catch (Exception ex) {
            try {
                Field f = t.getClass().getDeclaredField("validity");
                if (null == f) throw new RuntimeException("未定义[validity]逻辑删除字段");
                f.setAccessible(true);
                f.set(t, 0);
                f.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("未定义[validity]逻辑删除字段");
            }
        }
        baseMapper.update(t, new Wrapper<T>() {
            @Override
            public T getEntity() {
                try {

                    T conditionT = (T) t.getClass().newInstance();
                    Field filedId = t.getClass().getDeclaredField("id");
                    filedId.setAccessible(true);
                    filedId.set(conditionT, Long.valueOf(id+""));
                    filedId.setAccessible(false);

                    return conditionT;

                } catch (Exception e) {
                    //e.printStackTrace();
                }

                return null;
            }

            @Override
            public String getSqlSegment() {
                return null;
            }
        });
        return true;
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
    public PageInfo getPageInfo(Integer pageNo, Integer pageSize, Serializable example) {

        if (null == pageNo || pageNo <= 0) pageNo = 1;
        if (null == pageSize || pageSize <= 0) pageSize = 10;

        PageHelper.startPage(pageNo, pageSize);
        List<T> list = baseMapper.selectByExample(example);
        PageInfo pageInfo = PageInfo.getPageInfo(list);
        List<DTO> resultList = new ArrayList<>();
        if (null != list) {
            for (T t : list) {
                DTO dto = entityToDto(t);
                resultList.add(dto);
            }
        }
        pageInfo.setDataList(resultList);
        int currentNum = pageNo * pageSize;
        pageInfo.setHasMore(currentNum < pageInfo.getTotal());
        //处理total小于pageSize,pageSize自己变小的问题
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 根据id查询对象
     *
     * @param id
     * @param dtoClassName
     * @return
     */
    @Override
    public DTO getById(Serializable id, String dtoClassName) {
        return getById(id);
    }

    /**
     * 根据id列表查询对象列表
     *
     * @param idList
     * @param dtoClassName
     * @return
     */
    @Override
    public List<DTO> getListByIds(Collection<? extends Serializable> idList, String dtoClassName) {
        return getListByIds(idList);
    }

    /**
     * 根据example查询
     *
     * @param example
     * @param dtoClassName
     * @return
     */
    @Override
    public List<DTO> getList(Serializable example, String dtoClassName) {
        return getList(example);
    }
}
