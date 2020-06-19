package com.leyou.service;

import com.leyou.item.pojo.SpecParam;
import com.leyou.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecParamService {
    @Autowired
    SpecParamMapper spm;

    public List<SpecParam> findSpecParamById(Integer gid) {
        SpecParam sp = new SpecParam();
        sp.setGroupId((long) gid);
        return spm.select(sp);
    }

    public void addSpecParamByObject(SpecParam specParam) {
        spm.insert(specParam);
    }

    public void UpdateSpecParamByObject(SpecParam specParam) {
        spm.updateByPrimaryKey(specParam);
    }

    public void DeleteSpecParamByLongId(Long id) {
        spm.deleteByPrimaryKey(id);
    }


    public List<SpecParam> queryByGid(Long gid, Long cid, Boolean search, Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(search);
        specParam.setGeneric(generic);
        List<SpecParam> select = spm.select(specParam);
        return select;
    }
}
