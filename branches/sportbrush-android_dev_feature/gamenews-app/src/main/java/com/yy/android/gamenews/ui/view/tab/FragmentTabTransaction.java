package com.yy.android.gamenews.ui.view.tab;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class FragmentTabTransaction {

	private FragmentTransaction mTransaction;

	public static FragmentTabTransaction beginTransaction(
			FragmentActivity activity) {
		FragmentTransaction transaction = activity.getSupportFragmentManager()
				.beginTransaction();

		return new FragmentTabTransaction(transaction);
	}

	private FragmentTabTransaction(FragmentTransaction transaction) {
		mTransaction = transaction;
	}

	FragmentTransaction getTransaction() {
		return mTransaction;
	}

	public FragmentTabTransaction add(FrameFragmentItem tab) {
		if (tab != null) {
			tab.add(this);
		}
		return this;
	}

	public FragmentTabTransaction show(FrameFragmentItem tab) {
		if (tab != null) {
			tab.show(this);
		}
		return this;
	}

	public FragmentTabTransaction attach(FrameFragmentItem tab) {
		if (tab != null) {
			tab.attach(this);
		}
		return this;
	}

	public FragmentTabTransaction detach(FrameFragmentItem tab) {
		if (tab != null) {
			tab.detach(this);
		}
		return this;
	}

	public FragmentTabTransaction hide(FrameFragmentItem tab) {
		if (tab != null) {
			tab.hide(this);
		}
		return this;
	}

	public FragmentTabTransaction remove(FrameFragmentItem tab) {
		if (tab != null) {
			tab.remove(this);
		}
		return this;
	}

	public FragmentTabTransaction replace(FrameFragmentItem tab) {
		if (tab != null) {
			tab.replace(this);
		}
		return this;
	}

	public void commit() {
		mTransaction.commitAllowingStateLoss();
	}
}
