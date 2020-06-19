package cn.jiyun.common.pojo;

import java.util.List;

public class PageResult<T> {

    /*
    * 因为有很多微服务可能要用到分页 要用到这个类 所以要写到这里
    * */


    private Long total;  //总数据量

    private Integer totalPage; //总页数

    private List<T> items; //当前页的所有的数据

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

    public PageResult() {
    }

    public PageResult(long total, com.github.pagehelper.Page<T> pageInfo) {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", totalPage=" + totalPage +
                ", items=" + items +
                '}';
    }
}
