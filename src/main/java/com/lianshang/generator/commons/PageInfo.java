package com.lianshang.generator.commons;

import java.io.Serializable;
import java.util.ArrayList;
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
	public List getDataList() {
		return dataList;
	}
	public void setDataList(List list) {
		this.dataList = list;
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
	 * 获取泛型对象
	 * @param tClass
	 * @return
	 */
	public PageInfo<T> getPageInfo(Class<T> tClass) {
		PageInfo pageInfo = this;
		List list = pageInfo.getDataList();
		List<T> dataList = new ArrayList<>();
		if(null != list){
			for(Object o : list){
				String jsonValue = JsonUtils.object2JsonString(o);
				T t = JsonUtils.json2Object(jsonValue, tClass);
				dataList.add(t);
			}
		}
		pageInfo.setDataList(dataList);
		return pageInfo;
	}
	@Override
	public String toString() {
		return "PageInfo [pageNo=" + pageNo + ", pageSize=" + pageSize + ", total=" + total + ", pages=" + pages
				+ ", dataList=" + dataList + "]";
	}
	
}
