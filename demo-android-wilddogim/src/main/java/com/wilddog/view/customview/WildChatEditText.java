package com.wilddog.view.customview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.wilddog.views.emoji.EmojiconEditText;

/**
 * Created by Charles on 2016/4/25.
 */
public class WildChatEditText extends EmojiconEditText {
    private Drawable dRight;
    private Rect rBounds;
    public WildChatEditText(Context context) {
        super(context);
    }

    public WildChatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WildChatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top,
                                     Drawable right, Drawable bottom)
    {
        if(right !=null)
        {
            dRight = right;

        }
        super.setCompoundDrawables(left, top, right, bottom);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_UP && dRight!=null)
        {
            rBounds = dRight.getBounds();
            final int x = (int)event.getX();
            final int y = (int)event.getY();
            //System.out.println("x:/y: "+x+"/"+y);
            //System.out.println("bounds: "+bounds.left+"/"+bounds.right+"/"+bounds.top+"/"+bounds.bottom);
            //check to make sure the touch event was within the bounds of the drawable
            if(x>=(this.getRight()-rBounds.width()-this.getLeft()) && x<=(this.getRight()-this.getPaddingRight()-this.getLeft())
                    && y>=this.getPaddingTop() && y<=(this.getHeight()-this.getPaddingBottom()))
            {
                //System.out.println("touch");
                mCallBack.drawableOnClick();
                event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
            }
        }
        return super.onTouchEvent(event);
    }

    private DrawableOnClickListener mCallBack;
    public void setDrawableOnClickListener(DrawableOnClickListener callBack){
        mCallBack=callBack;
    }

    public interface DrawableOnClickListener {
        void drawableOnClick();
    }

    @Override
    protected void finalize() throws Throwable
    {
        dRight = null;
        rBounds = null;
        super.finalize();
    }
}
