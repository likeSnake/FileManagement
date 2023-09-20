package net.management.utility.file.manager.util;

import android.os.CountDownTimer;

public class TimerUtil {
    private CountDownTimer countDownTimer;

    public TimerUtil(long millisInFuture, long countDownInterval, TimerListener listener) {
        long millis = millisInFuture * 1000;
        countDownTimer = new CountDownTimer(millis, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                listener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                listener.onFinish();
            }
        };
    }

    public void start() {
        countDownTimer.start();
    }

    public void cancel() {
        countDownTimer.cancel();

    }

    public interface TimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }
}
