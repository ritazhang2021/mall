/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.rita.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	
	public R() {
		put("code", 0);
		put("msg", "success");
	}
/*	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}*/

	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public Integer getCode() {
		//因为R定义的是 public class R extends HashMap<String, Object>
		//所以拿到的code 是 Object code = this.get("code");
		//先将Object转为string，没有什么是不能转string的，再转integer
		return (Integer)this.get("code");
	}
	public R setData(Object data){
		//接收数据，转为Object，放到R中，就是map形式
		put("data", data);
		return this;
	}
	//利用阿里提供的fastjson进行逆转
	public <T> T getData(TypeReference<T> typeReference){
		//取出data, 将里面的object类型转为json格式的string,再转为自己需要的对象
		Object data = get("data");
		String s = JSON.toJSONString(data);
		T t = JSON.parseObject(s, typeReference);
		return t;
	}


}
