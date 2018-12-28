package com.lianshang.generator.commons;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lianshang.utils.FastJsonUtils;
import com.lianshang.utils.GenericsUtils;
import com.lianshang.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
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
    public static final int BATCH_UPDATE_MAX = 2000;//批量更新上线
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
     * 获取dto的ID
     *
     * @param dto
     * @return
     */
    private Object getIdByDto(DTO dto) {
        try {
            Field dtoId = dto.getClass().getDeclaredField("id");
            dtoId.setAccessible(true);
            Object idValue = dtoId.get(dto);
            dtoId.setAccessible(false);
            return idValue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
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

        T entity = dtoToEntity(n);

        List<DTO> dtoList = getList(example);
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new RuntimeException("批量更新的数据不能为空!");
        }

        int len = dtoList.size();
        if(len >= BATCH_UPDATE_MAX){
            throw new RuntimeException("批量更新的数据数量太大,锁表时间太长会影响其他事务的执行,请进行数据分片!");
        }

        StringBuilder whereSql = new StringBuilder(" where id in (");
        int i = 0;
        for (DTO dto : dtoList) {
            Object idValue = getIdByDto(dto);
            if (null != idValue) {
                i++;
                whereSql.append(idValue);
                if (i < len) {
                    whereSql.append(",");
                }
            }
        }
        whereSql.append(")");

        int r = baseMapper.update(entity, new Wrapper<T>() {
            @Override
            public T getEntity() {
                return null;
            }

            @Override
            public String getSqlSegment() {
                return whereSql.toString();
            }
        });
        return r > 0;
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
        if(null == t){
            return null;
        }
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

        Serializable exampleObj = serializableToExample(example);

        List<T> list = baseMapper.selectByExample(exampleObj);
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
     * serializable 转真实example对象
     *
     * @param example
     * @return
     */
    private Serializable serializableToExample(Serializable example) {

        Object exampleObj = null;
        if (null != example) {
            //获取真实的example
            Type type = this.getClass().getGenericInterfaces()[0];
            Class superClass = null;
            try {
                superClass = Class.forName(type.getTypeName());
            } catch (Exception ex) {

            }
            if (null == superClass) {
                return null;
            }
            //获取dto类
            Class dtoClass = getGenericTypeClass(superClass);
            Class exampleClass = getExampleClassByDtoClass(dtoClass);

            if (example.getClass() == exampleClass) {//无需转换
                return example;
            }
            if (example instanceof String) {
                String value = (String) example;
                log.info("jsonValue=>{}", value);

//                if (value.startsWith("\\{") && value.endsWith("\\}")) {
                    exampleObj = FastJsonUtils.convertJSONToObject(value,exampleClass);
//                      FastJsonUtils.convertJSONToObject(value, exampleClass);
                    return (Serializable)exampleObj;
//                }
            }
            exampleObj = JsonUtils.json2Object(JsonUtils.object2JsonString(example), exampleClass);
        }
        return (Serializable) exampleObj;
    }
    /**
     * 根据example查询总数量
     *
     * @param example
     * @return
     */
    @Override
    public int getCount(Serializable example) {
        Serializable exampleObj = serializableToExample(example);
        int count = 0;
        List<DTO> list = getList(exampleObj);
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
        Serializable exampleObj = serializableToExample(example);

        Page page = PageHelper.startPage(pageNo, pageSize);

        List<T> list = baseMapper.selectByExample(exampleObj);
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


    /**
     * 获取泛型名称
     *
     * @param thisObjClass
     */
    private static Class getGenericTypeClass(Class thisObjClass) {

        Class genericClass = null;
        if (null != thisObjClass) {
            genericClass = GenericsUtils.getSuperClassGenricType(thisObjClass);
        }

        if (null == genericClass || genericClass == Object.class) {
            return null;
        }

        return genericClass;
    }

    /**
     * 根据dto class获取example class
     *
     * @param dtoClass
     * @return
     */
    private static Class getExampleClassByDtoClass(Class dtoClass) {
        String className = dtoClass.getName();
        className = className.replaceAll("dto\\.", "example\\.");
        className = className.replaceAll("Dto$", "Example");
        try {
            Class exampleClass = Class.forName(className);
            return exampleClass;
        } catch (Exception ex) {
            return null;
        }
    }
}
