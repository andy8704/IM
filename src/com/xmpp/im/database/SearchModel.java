package com.xmpp.im.database;

/**
 * 
 * @ClassName: EJ_DBSearchModel
 * @Description: 查询的基本参数设置
 * @author andy.xu
 * @date 2014-3-6 上午11:32:12
 * 
 */
public class SearchModel {

	public int nPageSize;
	public int nPageIndex;
	public String whereStr;

	public SearchModel() {
		nPageSize = 20;
		nPageIndex = 0;
		whereStr = null;
	}

	/**
	 * 构造一个克隆的数据
	 * 
	 * @param model
	 * @return
	 */
	public static SearchModel onClone(final SearchModel model) {
		if (null == model)
			return null;

		SearchModel key = new SearchModel();
		key.nPageIndex = model.nPageIndex;
		key.nPageSize = model.nPageSize;
		return key;
	}
}
