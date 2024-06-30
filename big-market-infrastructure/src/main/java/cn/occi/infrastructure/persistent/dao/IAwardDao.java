package cn.occi.infrastructure.persistent.dao;

import cn.occi.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface IAwardDao {

    List<Award> queryAwardList();

}
