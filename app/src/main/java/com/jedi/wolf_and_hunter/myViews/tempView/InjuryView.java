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
    public float attackFromAngle;
    public int centerX;
    public int centerY;
    public long createTime = new Date().getTime();
    public boolean hasAddedToBaseFrame = false;
    public int viewSize;
    private static Bitmap bloodForMyCharacterBitmap;
    private static Bitmap bloodForOtherCharacterBitmap;

    public InjuryView(Context context, BaseCharacterView bindingCharacter,float attackFromAngle) {
        super(context);
        this.bindingCharacter = bindingCharacter;
        this.attackFromAngle=attackFromAngle;
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
            viewSize = (int) (bindingCharacter.characterBodySize * 6);
        }

        this.setLayoutParams(layoutParams);
        Bitmap originBloodBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blood);
        Bitmap originBloodBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.blood2);

        if (bloodForMyCharacterBitmap == null) {
            Matrix matrix = new Matrix();
            matrix.postScale((float) (viewSize) / originBloodBitmap.getWidth(), (float) (viewSize) / originBloodBitmap.getHeight());
            bloodForMyCharacterBitmap = Bitmap.createBitmap(originBloodBitmap, 0, 0, originBloodBitmap.getWidth(), originBloodBitmap.getHeight(), matrix, true);

        }
        if (bloodForOtherCharacterBitmap == null) {
            Matrix matrix = new Matrix();
            matrix.postScale((float) (viewSize) / originBloodBitmap2.getWidth(), (float) (viewSize) / originBloodBitmap2.getHeight());
            bloodForOtherCharacterBitmap = Bitmap.createBitmap(originBloodBitmap2, 0, 0, originBloodBitmap2.getWidth(), originBloodBitmap2.getHeight(), matrix, true);
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
            canvas.drawBitmap(bloodForMyCharacterBitmap, 0, 0, alphaPaint);
        else {
            canvas.rotate(attackFromAngle-45,viewSize/2,viewSize/2);
            canvas.drawBitmap(bloodForOtherCharacterBitmap, 0, 0, alphaPaint);
        }
        invalidate();
    }
}
