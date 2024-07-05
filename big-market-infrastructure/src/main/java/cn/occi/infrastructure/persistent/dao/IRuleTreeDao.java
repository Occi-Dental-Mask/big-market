package cn.occi.infrastructure.persistent.dao;

import cn.occi.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description:
 * @author: occi
 * @date: 2024/7/5
 */
@Mapper
public interface IRuleTreeDao {

    RuleTree queryRuleTreeByTreeId(String id);
}
