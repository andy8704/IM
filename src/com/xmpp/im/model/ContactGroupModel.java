package com.xmpp.im.model;

import java.io.Serializable;

import android.text.TextUtils;

/**
 * 
 * 
 * @类名称: ContactGroupModel
 * @描述： 联系人的分组
 * @开发者: andy.xu
 * @时间: 2014-8-25 下午2:53:59
 * 
 */
public class ContactGroupModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6528999395512225865L;

	/**
	 * 名称
	 */
	public String name;

	@Override
	public boolean equals(Object o) {
		if (null == o)
			return false;
		return TextUtils.equals(((ContactGroupModel) o).name, name);
	}

}
