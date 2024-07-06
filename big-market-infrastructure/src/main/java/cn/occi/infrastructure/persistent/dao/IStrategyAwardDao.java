package cn.occi.infrastructure.persistent.dao;

import cn.occi.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);

    String queryStrategyRule(Long strategyId, Integer awardId);

    void updateStrategyAwardStock(StrategyAward strategyAward);
}
