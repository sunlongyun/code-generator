package com.lianshang.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 描述:
 * 获取泛型类型
 * @AUTHOR 孙龙云
 * @date 2018-12-17 下午6:06
 */
@Slf4j
public class GenericsUtils {

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
     * GenricManager<Book>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or <code>Object.class</code> if cannot be determined
     */
    public static Class getSuperClassGenricType(Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager<Book>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     */
    public static Class getSuperClassGenricType(Class clazz, int index)
      throws IndexOutOfBoundsException {

        Type genType = clazz.getGenericSuperclass();
        if (null == genType || genType.getTypeName() == Object.class.getName()) {
            genType = clazz.getGenericInterfaces()[0];
        }
        if(null == genType) return clazz;

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        ParameterizedType parameterizedType = ((ParameterizedType) genType);
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }
}
