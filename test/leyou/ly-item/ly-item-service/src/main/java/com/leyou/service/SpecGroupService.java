package com.leyou.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecGroupService {

    @Autowired
    private SpecGroupMapper sgm;
    @Autowired
    private SpecParamMapper spm;

    public List<SpecGroup> findSpecGroupByCid(Long cid) {
        SpecGroup sg = new SpecGroup();
        sg.setCid(cid);
        List<SpecGroup> sList = sgm.select(sg);
        for (SpecGroup specGroup : sList) {
            List<SpecParam> specGroupBySpecGroupId = spm.findSpecGroupBySpecGroupId(specGroup.getId());
            specGroup.setParams(specGroupBySpecGroupId);
        }
        return sList;
    }

    public void updateSpecGroupById(SpecGroup specGroup) {
        //这里直接通过通用Mapper进行修改数据
        sgm.updateByPrimaryKeySelective(specGroup);
    }

    public void addSpecGroupOne(SpecGroup specGroup) {
        //添加
        sgm.insertSelective(specGroup);
    }

    public void paramsById(Long gid) {
        //删除
        sgm.deleteByPrimaryKey(gid);
    }
}
