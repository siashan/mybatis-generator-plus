package org.mybatis.generator.support.base.mapper;

import java.io.Serializable;
import java.util.List;

public interface BaseMapper<T, E> {

    long countByExample(E example);


    int deleteByPrimaryKey(Serializable pk);

    int insert(T record);

    int insertSelective(T record);

    List<T> selectByExample(E example);

    T selectByPrimaryKey(Serializable pk);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}