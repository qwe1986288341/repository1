package com.leyou.item.pojo;

import javax.persistence.Table;

@Table(name = "tb_category_brand")
public class BrandAndCategory {

    private Integer bid;

    private Integer cid;

    public BrandAndCategory() {
    }

    public Integer getBid() {
        return bid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }
}
