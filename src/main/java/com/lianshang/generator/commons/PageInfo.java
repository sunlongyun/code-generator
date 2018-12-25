package com.lianshang.generator.commons;

import com.lianshang.utils.JsonUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 分页
 *
 * @author 孙龙云
 */

public class PageInfo<T> implements Serializable {

	private static final long serialVersionUID = 1727384513155705568L;
	private Integer pageNo;
	private Integer pageSize;
	private Long total;
	private Integer pages;
	private List dataList;
	private String originClassName = LinkedHashMap.class.getName();
	private Boolean hasMore = true;

	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	
	public Integer getPages() {
		return pages;
	}
	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public String getOriginClassName() {
		return originClassName;
	}

	/**
	 * 解析成其他类型
	 * @param tClass
	 * @param <X>
	 * @return
	 */
	public <X> List<X> getDataList(Class<X> tClass) {
		List<X> list = new ArrayList<>();
		if(null != dataList){
			for(Object o : dataList){
				String jsonValue = JsonUtils.object2JsonString(o);
				X t = JsonUtils.json2Object(jsonValue, tClass);
				list.add(t);
			}
		}
		return list;
	}

	/**
	 * 获取原始类型,解决远程服务调用类型丢失的问题
	 * @return
	 */
	public List<T> getDataList(){
		List<T> list = new ArrayList<>();
		if(null != dataList){
			Class tClass =null;
			try {
				tClass = Class.forName(originClassName);
			}catch (Exception ex){
				tClass = LinkedHashMap.class;
			}
			for(Object o : dataList){
				String jsonValue = JsonUtils.object2JsonString(o);
				T t = (T) JsonUtils.json2Object(jsonValue, tClass);
				list.add(t);
			}
		}
		return list;
	}
	public void setDataList(List list) {
		this.dataList = list;
		if(!CollectionUtils.isEmpty(list)){
			originClassName = list.get(0).getClass().getName();
		}
	}

	public Boolean getHasMore() {
		return hasMore;
	}

	public void setHasMore(Boolean hasMore) {
		this.hasMore = hasMore;
	}

	private PageInfo(){
	}
	/**
	 * 获取分页对象
	 * @param list
	 * @return
	 */
	public  static <M> PageInfo getPageInfo(List<M> list){
		com.github.pagehelper.PageInfo<M> pageInfo = new com.github.pagehelper.PageInfo<>(list);
		
		PageInfo pInfo = new PageInfo();
		pInfo.setPageNo(pageInfo.getPageNum());
		pInfo.setPageSize(pageInfo.getPageSize());
		pInfo.setPages(pageInfo.getPages());
		pInfo.setTotal(pageInfo.getTotal());
		List<M> ll = new ArrayList<>();
		ll.addAll(pageInfo.getList());
		pInfo.setDataList(ll);
		pInfo.setHasMore(pageInfo.getPageNum() * pageInfo.getPageSize() < pageInfo.getTotal());
		return pInfo;
	}

	/**
	 * 根据泛型类,反序列对象
	 * @param tClass
	 * @return
	 */
	public PageInfo<T> getPageInfo(Class<T> tClass) {
		PageInfo pageInfo = this;
		List<T> list = pageInfo.getDataList(tClass);
		pageInfo.setDataList(list);
		return pageInfo;
	}
	@Override
	public String toString() {
		return "PageInfo [pageNo=" + pageNo + ", pageSize=" + pageSize + ", total=" + total + ", pages=" + pages
				+ ", dataList=" + dataList + "]";
	}
	
}
