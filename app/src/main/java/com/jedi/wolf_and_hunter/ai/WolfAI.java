package com.jedi.wolf_and_hunter.ai;

import android.graphics.Point;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Random;

/**
 * Created by Administrator on 2017/5/23.
 */

public class WolfAI extends BaseAI {
    public WolfAI(BaseCharacterView character) {
        super(character);
    }

    @Override
    public void addFacingThread() {
        if (facingThread == null) {
            facingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (bindingCharacter == null || bindingCharacter.isDead) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        synchronized (bindingCharacter) {
                            if (GameBaseAreaActivity.isStop == true)
                                break;


                            float relateAngle = targetFacingAngle - bindingCharacter.nowFacingAngle;
                            if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
                                if (relateAngle > 0)
                                    relateAngle = relateAngle - 360;

                                else
                                    relateAngle = 360 - relateAngle;
                            }
                            if (Math.abs(relateAngle) > angleChangSpeed)
                                relateAngle = Math.abs(relateAngle) / relateAngle * angleChangSpeed;

                            bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + relateAngle;
                            if (bindingCharacter.nowFacingAngle < 0)
                                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + 360;
                            else if (bindingCharacter.nowFacingAngle > 360)
                                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle - 360;

                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            facingThread.setDaemon(true);
            facingThread.start();
        }
    }

    @Override
    public void run() {
        super.run();

    }

    public void decideWhatToDo() {
        super.decideWhatToDo();
    }

    @Override
    public void trackTrajectory() {
        super.trackTrajectory();
    }

    @Override
    public void trackCharacter() {
        super.trackCharacter();
    }

    public void reset() {
        super.reset();
    }

    @Override
    public void attack() {
        super.attack();
    }

    @Override
    public void hunt() {
        super.hunt();
    }

    @Override
    public void escape() {
        super.escape();
    }
}
