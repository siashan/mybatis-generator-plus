package org.mybatis.generator.test.service;

import org.mybatis.generator.support.base.service.BaseService;
import org.mybatis.generator.test.mapper.TestMapper;
import org.mybatis.generator.test.model.Test;
import org.mybatis.generator.test.model.TestExample;
import org.springframework.stereotype.Service;

/**
 * <p>
    * 测试 服务实现类
    * </p>
 *
 * @author Small
 * @since 2018-05-15
 */
@Service
public class TestService extends BaseService<TestMapper,Test,TestExample>  {

}

