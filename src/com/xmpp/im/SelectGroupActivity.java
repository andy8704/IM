package com.xmpp.im;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xmpp.im.adapter.SelectGroupAdaper;
import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.model.ContactGroupModel;
import com.xmpp.im.util.IMCommDefine;

/**
 * 
 * 
 * @类名称: SelectGroupActivity
 * @描述: 选择一个分组
 * @开发者: andy.xu
 * @时间: 2014-8-28 上午10:53:33
 * 
 */
public class SelectGroupActivity extends IMBasicActivity {

	private TextView mBackBtn = null;
	private ListView mListView = null;
	private SelectGroupAdaper mAdapter = null;
	private List<ContactGroupModel> mGroupList = null;
	private ContactGroupModel mSelectModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_group_activity);

		mSelectModel = (ContactGroupModel) getIntent().getSerializableExtra(IMCommDefine.intent_data);
		mGroupList = ImUtil.getContactGroup();

		initUI();
	}

	private void initUI() {

		mBackBtn = (TextView) findViewById(R.id.back_btn_id);
		mListView = (ListView) findViewById(R.id.listview_id);

		mAdapter = new SelectGroupAdaper(this, mGroupList);
		mAdapter.onSetSelect(mSelectModel);
		mListView.setAdapter(mAdapter);

		mBackBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ContactGroupModel model = mGroupList.get(arg2);
				if (null != model) {
					Intent intent = new Intent();
					intent.putExtra(IMCommDefine.intent_data, model);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
