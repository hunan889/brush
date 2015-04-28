package com.yy.android.gamenews.ui.view.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class FrameFragmentItem {
	private FrameButton mBtn;
	private Fragment mFragment;
	private String mName;
	private boolean mSelectable = true;
	private boolean mIsVisible;

	public FrameFragmentItem() {
		this(null, null, null);
	}

	public FrameFragmentItem(FrameButton btn, Fragment fragment, String name) {
		mBtn = btn;
		mFragment = fragment;
		mName = name;
	}

	public boolean isSelectable() {
		return mSelectable;
	}

	public void setSelectable(boolean selectable) {
		this.mSelectable = selectable;
	}

	public boolean isVisible() {
		return mIsVisible;
	}

	public void setVisible(boolean isVisible) {
		this.mIsVisible = isVisible;
	}

	public void setButton(FrameButton btn) {
		mBtn = btn;
	}

	public FrameButton getButton() {
		return mBtn;
	}

	public Fragment getFragment() {
		return mFragment;
	}

	public void setFragment(Fragment fragment) {
		mFragment = fragment;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	void show(FragmentTabTransaction t) {
		if (mFragment != null) {
			FragmentTransaction transaction = t.getTransaction();
			transaction.show(mFragment);
		}
		mIsVisible = true;
		onShown();
	}

	void hide(FragmentTabTransaction t) {
		if (mFragment != null) {

			FragmentTransaction transaction = t.getTransaction();
			transaction.hide(mFragment);
		}
		mIsVisible = false;
		onHidden();
	}

	void add(FragmentTabTransaction t) {
		if (mFragment != null) {
			FragmentTransaction transaction = t.getTransaction();
			transaction.add(FrameFragmentLayout.CONTAINER_ID, mFragment,
					mName);
		}
	}

	void remove(FragmentTabTransaction t) {

		if (mFragment != null) {
			FragmentTransaction transaction = t.getTransaction();
			transaction.remove(mFragment);
		}
	}

	void attach(FragmentTabTransaction t) {

		if (mFragment != null) {
			FragmentTransaction transaction = t.getTransaction();
			transaction.attach(mFragment);

			// setFragment(initFragment());
		}
	}

	void detach(FragmentTabTransaction t) {

		if (mFragment != null) {
			FragmentTransaction transaction = t.getTransaction();
			transaction.detach(mFragment);

			// setFragment(initFragment());
		}
	}

	void replace(FragmentTabTransaction t) {
		FragmentTransaction transaction = t.getTransaction();
		transaction.replace(FrameFragmentLayout.CONTAINER_ID, mFragment);
	}

	protected void onShown() {
	}

	protected void onHidden() {
	}

	public void setOnClickListener(final OnClickListener listener) {
		if (mBtn != null) {
			mBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onClick(v);
					FrameFragmentItem.this.onItemClick();
				}
			});
		}
	}

	protected void onItemClick() {

	}
}
