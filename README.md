# code-generator

该组件是一款基于mybatis-plus开发的 集成 "通用代码生成","通用方法封装"的工具,旨在提高代码的开发效率.


一. CODE-GENERATOR 简单介绍

mybatis-plus简介:
#
无侵入：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
损耗小：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作
强大的 CRUD 操作：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
支持 Lambda 形式调用：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
支持多种数据库：支持 MySQL、MariaDB、Oracle、DB2、H2、HSQL、SQLite、Postgre、SQLServer2005、SQLServer 等多种数据库
支持主键自动生成：支持多达 4 种主键策略（内含分布式唯一 ID 生成器 - Sequence），可自由配置，完美解决主键问题
支持 XML 热加载：Mapper 对应的 XML 支持热加载，对于简单的 CRUD 操作，甚至可以无 XML 启动
支持 ActiveRecord 模式：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可进行强大的 CRUD 操作
支持自定义全局通用操作：支持全局通用方法注入（ Write once, use anywhere ）
支持关键词自动转义：支持数据库关键词（order、key......）自动转义，还可自定义关键词
内置代码生成器：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用
内置分页插件：基于 MyBatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通 List 查询
内置性能分析插件：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能快速揪出慢查询
内置全局拦截插件：提供全表 delete 、 update 操作智能分析阻断，也可自定义拦截规则，预防误操作
内置 Sql 注入剥离器：支持 Sql 注入剥离，有效预防 Sql 注入攻击
#



为何需要对MYBATIS-PLUS进一步分装?

1.1 mybatis-plus生成的service类是基于entity进行操作,实际项目中service层操作的对象应该是DTO.
     service的实现类中在把DTO转换为entity,调用DAO(或者mapper)来完成对数据库的访问.

1.2 mybatis-plus只支持单数据源,不支持动态数据源. (注: @ConditionalOnSingleCandidate(DataSource.class))

   mybatis-plus 初始化源码如下:
   @org.springframework.context.annotation.Configuration
   @ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
   @ConditionalOnSingleCandidate(DataSource.class)
   @EnableConfigurationProperties(MybatisPlusProperties.class)
   @AutoConfigureAfter(DataSourceAutoConfiguration.class)
   public class MybatisPlusAutoConfiguration {
    ....
   }

1.3 mybatis-plus生成的代码没有提供基于example的查询和批量更新功能.
1.4 mybatis-plus 没有提供pagehelper的分页功能




二. CODE-GENERATOR基于MYBATIS-PLUS升级之后,提供如下的功能:

2.1 自动生成entity,dto,mapper(DAO),service,service实现类,example(查询工具类)

2.2 封装了部分公共方法,service实现类可以直接使用.方法列表如下:

               Boolean save(Dto dto);//基于dto的添加
               Boolean update(Dto dto);//基于dto的更新
               Boolean batchUpdate(Dto dto, Serializable example);//基于dto和example批量更新
               Boolean deleteById(Serializable id);//基于id的逻辑删除,底层是修改validity状态
               Dto getById(Serializable id);//基于id查询dto
               List<Dto> getListByIds(Collection<? extends Serializable> idList);//基于id集合查询List<Dto>
               List<Dto> getList(Serializable example);//基于example查询dto列表
               int getCount(Serializable example);//基于example查询总数量
               PageInfo getPageInfo(Integer pageNo, Integer pageSize, Serializable example);//基于pagehelper,example进行分页也查询


2.3 支持动态数据源
  项目中如果需要使用多个数据源,只需要简单的配置,即可实现动态数据源切换.(跨数据源的事务暂时不支持)


2.4 对pageHelper的分页,基于example的查询提供了支持



三. CODE-GENERATOR使用步骤:


3.1 加入maven依赖

         <dependency>
            <groupId>com.lianshang</groupId>
            <artifactId>code-generator</artifactId>
        </dependency>


3.1 代码生成

    @Test
    public void test1(){
        //模块
        LsCodeGeneratorUtil.generateCode("store", "com.lianshang.cloth2.base",

          //数据库连接
          "jdbc:mysql://mysqldev.lsfash.cn:3307/cloth2?useUnicode=true&characterEncoding=utf8",
          "com.mysql.jdbc.Driver", "lsdev", "hcblihiNRqiy58rp",

          //这里是数据库表
          "cloth_store");
    }

3.3 加入mybatis-plus配置

application.properties或者apollo 加入 mybatis-plus.mapper-locations=classpath*:mapper/*.xml

加入mapper扫描, @MapperScan("com.lianshang.cloth2.base.*.mapper")


3.4配置动态数据源(动态数据源配置有点繁琐,后面会进行优化)

如果当前项目只有一个数据源,按照传统方式配置即可.插件默认使用该数据源.

如果需要多个数据源,那么配置如下:

(1)加入动态数据源切换切面

/**
 * 动态数据源切面
 * @author 孙龙云
 */
@Component
@Aspect
@Order(100)
public class DynamicDataSourceAspect {
    @Pointcut("execution(* com.lianshang.cloth2..service.impl..*(..)) || "
    		+ " execution(* com.lianshang.generator.commons..*(..))")
    private void beforeMethod() {

    }

    /**
     * 前置通知
     */
    @Before("beforeMethod()")
    public void beforeMethod(JoinPoint joinPoint) {
    	Class<?> clazz = joinPoint.getTarget().getClass();
    	try {
    		//包名称
    		Package pkg =  clazz.getPackage();
    		if(null != pkg){
    			String packageName =  pkg.getName();
    			if(StringUtils.isNotEmpty(packageName)){
    				DynamicDatasource.setDataSourceKey(packageName);
    			}
    		}
		} catch (Exception e) {
			//e.printStackTrace();
		}

    }

}

(2) 根据业务指明数据源,当同一个业务(service)匹配多个不同的数据源时,根据最长路径匹配规则进行数据源匹配.

@Component
public class PackageDataSourceCfgImpl implements PkgDtsCfg {

	@Override
	public Map<String, String> getConfig() {

		Map<String, String> map = new HashMap<>();

		map.put("com.lianshang.cloth2", "cloth_db");

		map.put("com.lianshang.cloth2.base.cloth", "cloth_db");
		map.put("com.lianshang.cloth2.base.customer", "customer_db");
		map.put("com.lianshang.cloth2.base.order", "cloth_db");
		map.put("com.lianshang.cloth2.base.common", "public_db");
		map.put("com.lianshang.cloth2.base.store", "cloth_db");
		map.put("com.lianshang.cloth2.base.report", "cloth_db");


		map.put("com.lianshang.cloth2.cloth.service", "cloth_db");
		map.put("com.lianshang.cloth2.customer.service", "customer_db");
		map.put("com.lianshang.cloth2.order.service", "cloth_db");
		map.put("com.lianshang.cloth2.common.service", "public_db");
		map.put("com.lianshang.cloth2.store.service", "cloth_db");
		map.put("com.lianshang.cloth2.report.service", "cloth_db");


		return map;
	}

}

最新版本 0.0.3
#1.修复了 PageInfo 的方法getDataList每次都要转换类型的bug.当前就是目标类型,不转换.
#2.生成代码工具类 增加了可部分覆盖文件的函数


