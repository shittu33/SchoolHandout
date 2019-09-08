package com.example.amazing_picker.Veiws;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;



public class ExtendedViewPager extends ViewPager {

	public ExtendedViewPager(Context context) {
	    super(context);
	}

	public ExtendedViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
	    if (v instanceof TouchImageView) {
	    	//
	    	// canScrollHorizontally is not supported for Api < 14. To get around this issue,
	    	// ViewPager is extended and canScrollHorizontallyFroyo, a wrapper around
	    	// canScrollHorizontally supporting Api >= 8, is called.
	    	//
	        return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);

	    } else {
	        return super.canScroll(v, checkV, dx, x, y);
	    }
	}

}
