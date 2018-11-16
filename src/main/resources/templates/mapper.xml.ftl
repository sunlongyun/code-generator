<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

<#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
<#list table.fields as field>
<#if field.keyFlag><#--生成主键排在第一位-->
        <id column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
<#list table.commonFields as field><#--生成公共字段 -->
    <result column="${field.name}" property="${field.propertyName}" />
</#list>
<#list table.fields as field>
<#if !field.keyFlag><#--生成普通字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
    </resultMap>
</#if>

<#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        <#list table.commonFields as field>
            ${field.name},
        </#list>
        ${table.fieldNames}
    </sql>

</#if>


    <!-- example通用查询-->
    <select id="selectByExample" parameterType="${cfg.examplePath}.${entity}Example" resultMap="BaseResultMap">
        select
        <if test="distinct">
            distinct
        </if>
        'false' as QUERYID,
        <include refid="Base_Column_List" />
        from ${table.name}
        <if test="_parameter != null">
            <include refid="Example_Where_Clause" />
        </if>
        <if test="orderByClause != null">
            order by ${'$'}{orderByClause}
        </if>
    </select>

    <!-- 条件-->
    <sql id="Example_Where_Clause">
        <where>
            <foreach collection="oredCriteria" item="criteria" separator="or">
                <if test="criteria.valid">
                    <trim prefix="(" prefixOverrides="and" suffix=")">
                        <foreach collection="criteria.criteria" item="criterion">
                            <choose>
                                <when test="criterion.noValue">
                                    and ${'$'}{criterion.condition}
                                </when>
                                <when test="criterion.singleValue">
                                    and ${'$'}{criterion.condition} ${'#'}{criterion.value}
                                </when>
                                <when test="criterion.betweenValue">
                                    and ${'$'}{criterion.condition} ${'#'}{criterion.value} and ${'#'}{criterion.secondValue}
                                </when>
                                <when test="criterion.listValue">
                                    and ${'$'}{criterion.condition}
                                    <foreach close=")" collection="criterion.value" item="listItem" open="(" separator=",">
                                    ${'#'}{listItem}
                                    </foreach>
                                </when>
                            </choose>
                        </foreach>
                    </trim>
                </if>
            </foreach>
        </where>
    </sql>
</mapper>
