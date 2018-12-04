package org.messmation.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.provider.SyncStateContract;
import android.util.AttributeSet;

import com.google.android.gms.common.internal.Constants;

public class TVFont extends android.support.v7.widget.AppCompatTextView {

    private AssetManager manager = getContext().getApplicationContext().getAssets();
    private Typeface nTypeface = Typeface.createFromAsset(manager, "fonts/segoeuisl.ttf");
    private Typeface bTypeface = Typeface.createFromAsset(manager, "fonts/seguisb.ttf" );

    public TVFont(Context context) {
        super(context);
    }

    public TVFont(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TVFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTypeface(Typeface tf, int s){
        if(s == Typeface.NORMAL){
            super.setTypeface(nTypeface);
        }
        else{
            super.setTypeface(bTypeface);
        }
    }
}
