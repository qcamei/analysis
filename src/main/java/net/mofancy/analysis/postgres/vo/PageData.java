package net.mofancy.analysis.postgres.vo;

/**
 * 分页
 * @author zwq
 */
public class PageData {
    private Long Total;  //记录总数
	private Integer PageSize;  //每面大小
    private Integer PageNum;  //当前页
    private Object dataList;  //数据对象
	public Integer getPageSize() {
		return PageSize;
	}
	public void setPageSize(Integer pageSize) {
		PageSize = pageSize;
	}
	public Long getTotal() {
		return Total;
	}
	public void setTotal(Long total) {
		Total = total;
	}
	public Integer getPageNum() {
		return PageNum;
	}
	public void setPageNum(Integer pageNum) {
		PageNum = pageNum;
	}
	public Object getDataList() {
		return dataList;
	}
	public void setDataList(Object dataList) {
		this.dataList = dataList;
	}
    
}