package com.jedi.wolf_and_hunter.myViews.tempView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/21.
 */

public class InjuryView extends View {
    BaseCharacterView bindingCharacter;
    Paint alphaPaint;
    public int centerX;
    public int centerY;
    public long createTime = new Date().getTime();
    public boolean hasAddedToBaseFrame = false;
    public int viewSize;
    private static Bitmap bloodBitmap;

    public InjuryView(Context context) {
        super(context);
        init();
    }

    private void init() {

        bindingCharacter = GameBaseAreaActivity.myCharacter;
        alphaPaint = new Paint();
        alphaPaint.setAlpha(0);
        alphaPaint.setStyle(Paint.Style.FILL);
        alphaPaint.setStrokeWidth(5);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int windowWidth = MyVirtualWindow.getWindowWidth(getContext());
        viewSize = (int) (windowWidth * 0.3);
        this.setLayoutParams(layoutParams);
        if (bloodBitmap == null) {


            bloodBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blood);
            Matrix matrix = new Matrix();
            matrix.postScale((float) (viewSize) / bloodBitmap.getWidth(), (float) (viewSize) / bloodBitmap.getHeight());
            bloodBitmap = Bitmap.createBitmap(bloodBitmap, 0, 0, bloodBitmap.getWidth(), bloodBitmap.getHeight(), matrix, true);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(viewSize, viewSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bindingCharacter == null || bindingCharacter.lastInjureTime < 0)
            return;
        long nowTime = new Date().getTime();
        long passTime = nowTime - createTime;
        long relateTime = bindingCharacter.nowRecoverTime - passTime;
        int alpha = 0;
        if (relateTime > 0)
            alpha = (int) (255 * relateTime / bindingCharacter.nowRecoverTime);
        alphaPaint.setAlpha(alpha);
        canvas.drawBitmap(bloodBitmap, 0, 0, alphaPaint);
        invalidate();
    }
}
