package com.yupi.yupicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.manager.auth.StpKit;
import com.yupi.yupicturebackend.models.domain.User;
import com.yupi.yupicturebackend.models.dto.user.UserQueryRequest;
import com.yupi.yupicturebackend.models.enums.UserRoleEnum;
import com.yupi.yupicturebackend.models.vo.user.LoginUserVO;
import com.yupi.yupicturebackend.models.vo.user.UserVO;
import com.yupi.yupicturebackend.service.UserService;
import com.yupi.yupicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yupi.yupicturebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author ljy
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-11-11 11:37:31
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        // 1. 校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号过短");
        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码过短");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        // 2. 检查是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR, "账号重复");

        // 3. 加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());

        boolean save = this.save(user);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "插入数据失败");

        return user.getId();
    }

    @Override
    public String getEncryptPassword(String userPassword) {

        // 盐值，混淆密码
        final String SALT = "yupi";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 1. 校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4 || userPassword.length() < 8,
                ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");

        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 3. 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 记录用户登录态到 Sa-token，便于空间鉴权时使用，注意保证该用户信息与 SpringSession 中的信息过期时间一致
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(UserConstant.USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {

        // 1. 判断是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(userObj == null, ErrorCode.NOT_LOGIN_ERROR);

        // 2. 清除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }


    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);

        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;

    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 判断是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(currentUser == null || currentUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);

        // 数据库查询，防止缓存的不一致，追求性能可注释
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_FOUND_ERROR);

        return currentUser;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }



}




