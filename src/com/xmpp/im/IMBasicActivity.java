package com.xmpp.im;

import android.app.Activity;
import android.os.Bundle;

import com.xmpp.im.util.FuncUtil;

/**
 * 
 * @ClassName: MinaBasciActivity
 * @Description: 基本类
 * @author andy.xu
 * @date 2014-3-14 下午4:58:38
 * 
 */
public class IMBasicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FuncUtil.addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		FuncUtil.popActivity(this);
	}
	
}
