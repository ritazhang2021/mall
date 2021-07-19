package com.rita.modules.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.rita.common.valid.AddGroup;
import com.rita.common.valid.ListValue;
import com.rita.common.valid.UpdateGroup;
import com.rita.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author Rita
 * @email rita2021.zhang@gmail.com
 * @date 2021-06-13 12:09:24
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 * groups是个数组
	 */
	@NotNull(message = "修改必须指定品牌id",groups = {UpdateGroup.class})
	@Null(message = "新增不能指定id",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotNull//对像中它的值可以为null
	@NotEmpty//空对象
	@NotBlank(message = "品牌必须填写",groups = { AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 * 修改时可以为空，一旦有值，就必须符合规定
	 */
	//controller中改用@Validated,并指定了group,所以这里也必须指定group，否则不生效，entity和controller要不都用group，要不都不用
	@URL(message = "logo必须是一个合法的地址",groups = { UpdateGroup.class, AddGroup.class})
	@NotEmpty(message = "logo不能为空",groups = {AddGroup.class})//update时logo可以为空，但必须是一个合法的url
	private String logo;
	/**
	 * 介绍
	 */
	@NotEmpty(message = "介绍不能为空")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
	@ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	//自定义校验regexp，在java中写是不需要//的
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母" ,groups = { UpdateGroup.class, AddGroup.class})
	@NotEmpty(message = "首字母不能为空",groups = {AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0, message = "排序必须大于等于0",groups = { UpdateGroup.class, AddGroup.class})
	//@NotEmpty 不可以使用这个，因为它只支持字符，array等
	@NotNull(message = "排序不能为空",groups = {  AddGroup.class})
	private Integer sort;

}
