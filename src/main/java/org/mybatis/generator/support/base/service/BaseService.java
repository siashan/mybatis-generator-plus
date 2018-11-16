package org.mybatis.generator.support.base.service;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.generator.support.base.mapper.BaseMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: kfc
 * @Description: <br/>
 * Date:Create in 2018/4/4 14:39
 * @Modified By:
 */
public class BaseService<M extends BaseMapper<T, E>, T, E> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected M baseMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;


    @Transactional
    public int insert(T entity) {
        return baseMapper.insert(entity);
    }

    @Transactional
    public int insertSelective(T entity) {
        return baseMapper.insertSelective(entity);
    }

    @Transactional
    public int updateByPrimaryKey(T entity) {
        return baseMapper.updateByPrimaryKey(entity);
    }

    public int updateByPrimaryKeySelective(T entity) {
        return baseMapper.updateByPrimaryKeySelective(entity);
    }

    public T selectByPrimaryKey(Serializable pk) {
        return baseMapper.selectByPrimaryKey(pk);
    }

    public List<T> selectByExample(E example) {
        return baseMapper.selectByExample(example);
    }

    @Transactional
    public int deleteByPrimaryKey(Serializable pk) {
        return baseMapper.deleteByPrimaryKey(pk);
    }

    public long countByExample(E example) {
        return baseMapper.countByExample(example);
    }

    public T selectOneByExample(E example) {
        List<T> ts = this.selectByExample(example);
        if (!ts.isEmpty()) {
            return ts.get(0);
        }
        return null;
    }

    @Transactional
    public void insertBatch(List<T> records) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            for (T record : records) {
                baseMapper.insert(record);
            }
            // 提交事务
            sqlSession.commit();
            // 清理缓存，防止溢出
            sqlSession.clearCache();
        }catch (Exception e) {
            //没有提交的数据可以回滚
            sqlSession.rollback();
        }finally {
            sqlSession.close();
        }

    }

    @Transactional
    public void insertBatch(List<T> records, int cnt) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            for (int i = 0; i < records.size();
                 i++) {
                baseMapper.insert(records.get(i));
                if (i % cnt == 0 || i == cnt - 1) {
                    // 提交事务
                    sqlSession.commit();
                    // 清理缓存，防止溢出
                    sqlSession.clearCache();
                }
            }
        } catch (Exception e) {
            //没有提交的数据可以回滚
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
    }
}
