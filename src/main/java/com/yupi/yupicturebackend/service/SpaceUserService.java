package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.models.domain.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.models.dto.spaceuser.SpaceUserAddRequest;
import com.yupi.yupicturebackend.models.dto.spaceuser.SpaceUserQueryRequest;
import com.yupi.yupicturebackend.models.vo.spaceuser.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ljy
 * @description 针对表【spaceUser(空间用户关联)】的数据库操作Service
 * @createDate 2025-11-24 19:05:53
 */
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 用户添加空间用户
     *
     * @param spaceUserAddRequest
     * @return 添加的空间用户id
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验 添加/修改空间用户时，空间用户对象的合法性
     *
     * @param spaceUser
     * @param add
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取查询条件
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取单条空间用户结果
     *
     * @param spaceUser
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取多条空间用户结果
     *
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);


}
