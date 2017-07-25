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
    private static Bitmap bigBloodBitmap;
    private Bitmap smallBloodBitmap;

    public InjuryView(Context context, BaseCharacterView bindingCharacter) {
        super(context);
        this.bindingCharacter = bindingCharacter;
        init();
    }

    private void init() {

        alphaPaint = new Paint();
        alphaPaint.setAlpha(0);
        alphaPaint.setStyle(Paint.Style.FILL);
        alphaPaint.setStrokeWidth(5);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (bindingCharacter.isMyCharacter) {
            int windowWidth = MyVirtualWindow.getWindowWidth(getContext());
            viewSize = (int) (windowWidth * 0.3);
        } else {
            viewSize = (int) (bindingCharacter.characterBodySize * 1.5);
        }

        this.setLayoutParams(layoutParams);
        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blood);
        if (bindingCharacter.isMyCharacter) {
            if (bigBloodBitmap == null) {
                Matrix matrix = new Matrix();
                matrix.postScale((float) (viewSize) / originBitmap.getWidth(), (float) (viewSize) / originBitmap.getHeight());
                bigBloodBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);

            }
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale((float) (viewSize) / originBitmap.getWidth(), (float) (viewSize) / originBitmap.getHeight());
            smallBloodBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);
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
        if (bindingCharacter.isMyCharacter)
            canvas.drawBitmap(bigBloodBitmap, 0, 0, alphaPaint);
        else
            canvas.drawBitmap(smallBloodBitmap, 0, 0, alphaPaint);

        invalidate();
    }
}
