package com.lianshang.generator.commons;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class ServiceImpl<M extends BaseMapper<T>, T, N> implements IService<N> {
    @Autowired
    protected M baseMapper;

    /**
     * x 对象转为 y 对象
     *
     * @param x
     * @return
     */
    private <Y> Object X2Y(Object x) {
        Y y = null;
        try {
            String xClassName = x.getClass().getName();
            String yClassName = xClassName.replaceAll("entity\\.","dto\\.")+"Dto";
            Object target = Class.forName(yClassName).newInstance();
            BeanUtils.copyProperties(x, target);
            return (Y) target;
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
    public boolean save(N n) {
        Object target = X2Y(n);
        int r = baseMapper.insert((T) target);
        return r > 0;
    }

    /**
     * 根据id修改对象
     *
     * @param id
     * @return
     */
    @Override
    public boolean updateById(Serializable id) {
        T t = baseMapper.selectById(id);
        int r = baseMapper.updateById(t);
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
    public N getById(Serializable id) {
        T t = baseMapper.selectById(id);
        N n = (N) X2Y(t);
        return n;
    }

    /**
     * 根据id列表查询对象列表
     *
     * @param idList
     * @return
     */
    @Override
    public List<N> getListByIds(Collection<? extends Serializable> idList) {
        List<T> list = baseMapper.selectBatchIds(idList);
        List<N> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(T t : list){
                N n = (N) X2Y(t);
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
    public List<N> getListByMap(Map<String, Object> map) {
        List<T> list =  baseMapper.selectByMap(map);
        List<N> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(T t : list){
                N n = (N) X2Y(t);
                resultList.add(n);
            }
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
    public int getCount(Map<String, Object> map) {

        return 0;
    }

    /**
     * 根据参数分页
     * @param pageNo
     * @param pageSize
     * @param map
     * @return
     */
    @Override
    public PageInfo<N> getPageInfo(int pageNo, int pageSize, Map<String, Object> map) {
        if(pageNo <= 0) pageNo = 1;
        if(pageSize <=0) pageSize=10;
        PageHelper.startPage(pageNo, pageSize);
        List<N> list = getListByMap(map);
        PageInfo<N> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}
