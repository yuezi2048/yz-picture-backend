package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.models.domain.Picture;
import com.yupi.yupicturebackend.models.domain.Space;
import com.yupi.yupicturebackend.models.domain.User;
import com.yupi.yupicturebackend.models.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.models.dto.space.SpaceAddRequest;
import com.yupi.yupicturebackend.models.dto.space.SpaceQueryRequest;
import com.yupi.yupicturebackend.models.vo.picture.PictureVO;
import com.yupi.yupicturebackend.models.vo.space.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ljy
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-11-20 15:38:59
 */
public interface SpaceService extends IService<Space> {

    /**
     * 用户添加空间
     *
     * @param spaceAddRequest
     * @param loginUser
     * @return 添加的空间id
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 校验 添加/修改空间时，空间对象的合法性
     *
     * @param space
     * @param add
     */
    void validSpace(Space space, boolean add);

    /**
     * 根据空间级别，填充空间对象相关信息（为空时填充）
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 获取查询条件
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取单条空间结果
     *
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取多条空间结果
     *
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);
}
