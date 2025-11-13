package com.yupi.yupicturebackend.models.dto.picture;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yupi.yupicturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
public class PictureUploadRequest implements Serializable {

    /**
     * 图片id（表示修改）
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
