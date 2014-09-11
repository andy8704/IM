package com.xmpp.im;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xmpp.im.client.util.ImUtil;
import com.xmpp.im.client.util.XmppTool;

/**
 * 登陆界面
 * 
 * @类名称: LoginActivity
 * @描述:
 * @开发者: andy.xu
 * @时间: 2014-8-26 上午10:50:38
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {

	private EditText useridText, pwdText;
	private LinearLayout layout1, layout2;
	private TextView mRegisterBtn = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formlogin);

		this.useridText = (EditText) findViewById(R.id.formlogin_userid);
		this.pwdText = (EditText) findViewById(R.id.formlogin_pwd);

		this.layout1 = (LinearLayout) findViewById(R.id.formlogin_layout1);
		this.layout2 = (LinearLayout) findViewById(R.id.formlogin_layout2);
		useridText.setText("xuhan");
		pwdText.setText("123");
		Button btsave = (Button) findViewById(R.id.formlogin_btsubmit);
		btsave.setOnClickListener(this);
		Button btcancel = (Button) findViewById(R.id.formlogin_btcancel);
		btcancel.setOnClickListener(this);
		
		mRegisterBtn = (TextView) findViewById(R.id.register_view_id);
		mRegisterBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.formlogin_btsubmit:
			final String USERID = this.useridText.getText().toString();
			final String PWD = this.pwdText.getText().toString();

			Thread t = new Thread(new Runnable() {
				public void run() {
					handler.sendEmptyMessage(1);
					try {
						XmppTool.getConnection().login(USERID, PWD);
						// 先获取离线的消息
						ImUtil.onSetOfflineMsg(XmppTool.getOffLineMessage());

						// 设置为在线状态
						XmppTool.onLine();
						XmppTool.onSetUserId(USERID);
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, MainActivity.class);
						LoginActivity.this.startActivity(intent);
						LoginActivity.this.finish();
					} catch (XMPPException e) {
						XmppTool.closeConnection();
						handler.sendEmptyMessage(2);
					}
				}
			});
			t.start();
			break;
		case R.id.formlogin_btcancel:
			finish();
			break;
		case R.id.register_view_id:
			Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			finish();
			break;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.GONE);
			} else if (msg.what == 2) {
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				Toast.makeText(LoginActivity.this, "登录异常", Toast.LENGTH_SHORT).show();
			}
		};
	};
}