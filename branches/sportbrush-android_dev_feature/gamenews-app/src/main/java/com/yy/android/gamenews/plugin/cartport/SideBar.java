package com.yy.android.gamenews.plugin.cartport;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yy.android.sportbrush.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SideBar extends View {
	/*
	 * private static final Handler sHandler; static { sHandler = new
	 * Handler(Looper.getMainLooper()); }
	 */
	private char[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private float m_nItemHeight = 0;
	private int FLAG = -1;
	private Paint paint = new Paint();
	private int TEXTSIZE = 20;
	private TextView mDialogText;
	private boolean showDown = false;

	public SideBar(Context context) {
		super(context);
		initialize();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		switch (getDpi(getContext())) {
		case 120:
			TEXTSIZE = 8;
			break;
		case 160:
			TEXTSIZE = 13;
			break;
		}
	}

	public void init(float higet) {
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(TEXTSIZE);
		paint.setTextAlign(Paint.Align.CENTER);
		Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
		paint.setTypeface(font);
		switch (getId()) {
		case R.id.mSideBar:
			l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
					'W', 'X', 'Y', 'Z' };
			break;
		default:
			l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
					'W', 'X', 'Y', 'Z' };

			break;
		}
		float i = (higet - (TEXTSIZE * (l.length + 2))) / (l.length + 2);
		m_nItemHeight = TEXTSIZE + i;
		this.invalidate();
	}

	public void setListView(ListView _list) {
		list = _list;
		if (list.getFooterViewsCount() != 0 || list.getHeaderViewsCount() != 0) {
			sectionIndexter = (SectionIndexer) (((HeaderViewListAdapter) list
					.getAdapter()).getWrappedAdapter());
		} else {
			sectionIndexter = (SectionIndexer) _list.getAdapter();
		}
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public int getHeight(Context context) {
		Rect rect = new Rect();
		Window win = ((Activity) context).getWindow();
		win.getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels - (statusBarHeight * 2);
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = (int) (i / m_nItemHeight);
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			showDown = true;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			list.invalidate();
			showDown = false;
		}

		FLAG = idx;
		this.invalidate();
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText("" + l[idx]);
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(l[idx]);
			if (position == -1) {
				return true;
			}
			list.setSelection(position);
			// sHandler.post(new Runnable() {
			//
			// @Override
			// public void run() {
			// list.scrollTo(0, (int) (list.getY() - 220));
			// }
			// });
		} else {
			FLAG = -1;
			this.invalidate();
			mDialogText.setVisibility(View.INVISIBLE);
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < l.length; i++) {
			if (FLAG == i) {
				paint.setColor(getContext().getResources().getColor(
						R.color.title));
			} else {
				if (showDown) {
					paint.setColor(getContext().getResources().getColor(
							R.color.scrollbar));
				} else {
					paint.setColor(getContext().getResources().getColor(
							R.color.title));
				}

			}
			canvas.drawText(String.valueOf(l[i]), widthCenter,
					(i * m_nItemHeight) + m_nItemHeight, paint);
		}
		super.onDraw(canvas);
	}

	private int getDpi(Context activity) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = activity.getResources().getDisplayMetrics();
		int densityDpi = dm.densityDpi; // 屏幕密度DPI 120 / 160 / 240
		return densityDpi;
	}

}
