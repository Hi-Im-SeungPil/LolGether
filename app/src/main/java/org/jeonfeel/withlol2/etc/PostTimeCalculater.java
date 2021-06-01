package org.jeonfeel.withlol2.etc;

import java.util.Date;

public class PostTimeCalculater {

        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;

    public static String formatTimeString(Date tempDate) {

        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;

        if (diffTime < PostTimeCalculater.SEC) {

            // sec
            msg = "방금 전";

        } else if ((diffTime /= PostTimeCalculater.SEC) < PostTimeCalculater.MIN) {

            // min
            msg = diffTime + "분 전";

        } else if ((diffTime /= PostTimeCalculater.MIN) < PostTimeCalculater.HOUR) {

            // hour
            msg = (diffTime) + "시간 전";

        } else if ((diffTime /= PostTimeCalculater.HOUR) < PostTimeCalculater.DAY) {

            // day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= PostTimeCalculater.DAY) < PostTimeCalculater.MONTH) {

            // day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
