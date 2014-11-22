package com.util;

import com.orient.R;

public class Portrait {
	private int[] images = { R.drawable.head, R.drawable.head2,
            R.drawable.head3, R.drawable.head4, R.drawable.head5,
            R.drawable.head6, R.drawable.head7, R.drawable.head8,
            };
	public Portrait() {
	}
	public int getPortraitResource(int position) {
		return images[position];
	}
}
