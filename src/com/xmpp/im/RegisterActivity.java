package com.xmpp.im;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xmpp.im.client.util.XmppTool;

/**
 * 
 * @类名称: RegisterActivity
 * @描述: 用户注册
 * @开发者: andy.xu
 * @时间: 2014-8-26 上午10:50:38
 * 
 */
public class RegisterActivity extends Activity implements OnClickListener {

	private EditText useridText, pwdText;
	private LinearLayout layout1, layout2;
	private TextView mLoginBtn = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_register_activity);

		this.useridText = (EditText) findViewById(R.id.formlogin_userid);
		this.pwdText = (EditText) findViewById(R.id.formlogin_pwd);

		this.layout1 = (LinearLayout) findViewById(R.id.formlogin_layout1);
		this.layout2 = (LinearLayout) findViewById(R.id.formlogin_layout2);
		Button btsave = (Button) findViewById(R.id.formlogin_btsubmit);
		btsave.setOnClickListener(this);
		Button btcancel = (Button) findViewById(R.id.formlogin_btcancel);
		btcancel.setOnClickListener(this);

		mLoginBtn = (TextView) findViewById(R.id.login_view_id);
		mLoginBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.formlogin_btsubmit:
			onRegister();
			break;
		case R.id.formlogin_btcancel:
			finish();
			break;
		case R.id.login_view_id:
			Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		}
	}

	private void onRegister() {

		final String USERID = this.useridText.getText().toString();
		final String PWD = this.pwdText.getText().toString();

		Thread t = new Thread(new Runnable() {
			public void run() {
				handler.sendEmptyMessage(0);
				XMPPConnection connect = XmppTool.getConnection();
				Presence presence = new Presence(Presence.Type.available);
				connect.sendPacket(presence);

				int nFlag = XmppTool.onRegist(connect, USERID, PWD);
				handler.sendEmptyMessage(nFlag);
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		t.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 0) {
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.GONE);
			} else if (msg.what == 3) {
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				Toast.makeText(RegisterActivity.this, "注册异常", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 2) {
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				Toast.makeText(RegisterActivity.this, "该账户已经存在", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 1) {
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.GONE);
				Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
			}
		};
	};
}