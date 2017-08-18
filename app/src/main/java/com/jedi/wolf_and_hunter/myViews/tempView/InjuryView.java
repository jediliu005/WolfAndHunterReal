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
import com.jedi.wolf_and_hunter.utils.BitmapBox;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/21.
 */

public class InjuryView extends View {
    BaseCharacterView bindingCharacter;
    private static Paint alphaPaint;
    public float attackFromAngle;
    public int centerX;
    public int centerY;
    public long createTime = new Date().getTime();
    public boolean hasAddedToBaseFrame = false;
    public int viewSize;
    private static Bitmap bloodOfMyCharacterBitmap;
    private static Bitmap bloodOfOtherCharacterBitmap;

    public InjuryView(Context context, BaseCharacterView bindingCharacter, float attackFromAngle) {
        super(context);
        this.bindingCharacter = bindingCharacter;
        this.attackFromAngle = attackFromAngle;
        init();
    }

    private void init() {
        if (alphaPaint == null) {
            alphaPaint = new Paint();
            alphaPaint.setAlpha(0);
            alphaPaint.setStyle(Paint.Style.FILL);
            alphaPaint.setStrokeWidth(5);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(layoutParams);
        }
        if (bindingCharacter.isMyCharacter) {
            int windowWidth = MyVirtualWindow.getWindowWidth(getContext());
            viewSize = (int) (windowWidth * 0.3);
        } else {
            viewSize = (int) (bindingCharacter.characterBodySize * 6);
        }

        if (bloodOfMyCharacterBitmap == null) {
            Bitmap oriBloodOfMyCharacter = BitmapBox.getOtherBitmap(getContext(),"oriBloodOfMyCharacter") ;
            Matrix matrix = new Matrix();
            matrix.postScale((float) (viewSize) / oriBloodOfMyCharacter.getWidth(), (float) (viewSize) / oriBloodOfMyCharacter.getHeight());
            bloodOfMyCharacterBitmap = Bitmap.createBitmap(oriBloodOfMyCharacter, 0, 0, oriBloodOfMyCharacter.getWidth(), oriBloodOfMyCharacter.getHeight(), matrix, true);

        }
        if (bloodOfOtherCharacterBitmap == null) {
            Bitmap oriBloodOfOtherCharacter =  BitmapBox.getOtherBitmap(getContext(),"oriBloodOfOtherCharacter") ;
            Matrix matrix = new Matrix();
            matrix.postScale((float) (viewSize) / oriBloodOfOtherCharacter.getWidth(), (float) (viewSize) / oriBloodOfOtherCharacter.getHeight());
            bloodOfOtherCharacterBitmap = Bitmap.createBitmap(oriBloodOfOtherCharacter, 0, 0, oriBloodOfOtherCharacter.getWidth(), oriBloodOfOtherCharacter.getHeight(), matrix, true);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(viewSize, viewSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bindingCharacter == null )
            return;
        long nowTime = new Date().getTime();
        long passTime = nowTime - createTime;
        long relateTime = bindingCharacter.nowRecoverTime - passTime;
        int alpha = 0;
        if (relateTime > 0)
            alpha = (int) (255 * relateTime / bindingCharacter.nowRecoverTime);
        alphaPaint.setAlpha(alpha);
        if (bindingCharacter.isMyCharacter)
            canvas.drawBitmap(bloodOfMyCharacterBitmap, 0, 0, alphaPaint);
        else {
            canvas.rotate(attackFromAngle - 45, viewSize / 2, viewSize / 2);
            canvas.drawBitmap(bloodOfOtherCharacterBitmap, 0, 0, alphaPaint);
        }
        invalidate();
    }
}
