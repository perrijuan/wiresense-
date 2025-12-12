package com.example.tensorflowlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class OverlayView extends View {
    private Bitmap maskBitmap;
    private final Rect destRect = new Rect();
    private final Paint paint = new Paint();

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setFilterBitmap(true);
    }

    public void setMask(Bitmap mask) {
        this.maskBitmap = mask;
        invalidate();
    }

    public void clear() {
        this.maskBitmap = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maskBitmap != null) {
            destRect.set(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(maskBitmap, null, destRect, paint);
        }
    }
}