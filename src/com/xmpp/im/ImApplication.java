package com.xmpp.im;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xmpp.im.database.XmppDB;

import android.app.Application;

public class ImApplication extends Application {

	private static ImApplication instance = null;

	private XmppDB mDB = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		instance = this;
		es = Executors.newFixedThreadPool(3);
	}

	public static ImApplication onGetInstance() {
		return instance;
	}

	public XmppDB onGetDB() {
		if (null == mDB)
			mDB = new XmppDB(getApplicationContext());
		return mDB;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	private ExecutorService es;

	public void execRunnable(Runnable r) {
		es.execute(r);
	}
}
