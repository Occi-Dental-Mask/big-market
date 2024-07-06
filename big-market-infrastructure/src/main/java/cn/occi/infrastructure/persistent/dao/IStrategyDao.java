package cn.occi.infrastructure.persistent.dao;

import cn.occi.domain.strategy.model.entity.StrategyEntity;
import cn.occi.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Fuzhengwei occi.cn @小傅哥
 * @description 抽奖策略 DAO
 * @create 2023-12-16 13:24
 */
@Mapper
public interface IStrategyDao {

    List<Strategy> queryStrategyList();

    Strategy queryStrategyByStrategyId(Long strategyId);


}
