package com.ksn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ksn.entity.Role;
import com.ksn.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/26 10:11
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
