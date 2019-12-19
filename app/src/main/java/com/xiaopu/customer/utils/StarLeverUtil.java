package com.xiaopu.customer.utils;

import android.util.Log;
import android.widget.ImageView;

import com.xiaopu.customer.R;

import static java.lang.Double.compare;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class StarLeverUtil {

    public static void setStarLv(double commentRate, ImageView ivStarOne,
                                 ImageView ivStarTwo, ImageView ivStarThree,
                                 ImageView ivStarFour, ImageView ivStarFive) {
        double a = 7;
        double b = 11;
        double num2 = (a / b);


        if (compare(commentRate, 3.0) + compare(commentRate, (3 + num2)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_null);
            ivStarTwo.setImageResource(R.mipmap.star_null);
            ivStarThree.setImageResource(R.mipmap.star_null);
            ivStarFour.setImageResource(R.mipmap.star_null);
            ivStarFive.setImageResource(R.mipmap.star_null);
        } else if (compare(commentRate, 3 + num2) + compare(commentRate, (3 + num2 * 2)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_part);
            ivStarTwo.setImageResource(R.mipmap.star_null);
            ivStarThree.setImageResource(R.mipmap.star_null);
            ivStarFour.setImageResource(R.mipmap.star_null);
            ivStarFive.setImageResource(R.mipmap.star_null);
        } else if (compare(commentRate, 3 + num2 * 2) + compare(commentRate, (3 + num2 * 3)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_null);
            ivStarThree.setImageResource(R.mipmap.star_null);
            ivStarFour.setImageResource(R.mipmap.star_null);
            ivStarFive.setImageResource(R.mipmap.star_null);
        } else if (compare(commentRate, 3 + num2 * 3) + compare(commentRate, (3 + num2 * 4)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_part);
            ivStarThree.setImageResource(R.mipmap.star_null);
            ivStarFour.setImageResource(R.mipmap.star_null);
            ivStarFive.setImageResource(R.mipmap.star_null);

        } else if (compare(commentRate, 3 + num2 * 4) + compare(commentRate, (3 + num2 * 5)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_full);
            ivStarThree.setImageResource(R.mipmap.star_null);
            ivStarFour.setImageResource(R.mipmap.star_null);
            ivStarFive.setImageResource(R.mipmap.star_null);

        } else if (compare(commentRate, 3 + num2 * 5) + compare(commentRate, (3 + num2 * 6)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_full);
            ivStarThree.setImageResource(R.mipmap.star_part);
            ivStarFour.setImageResource(R.mipmap.star_null);
            ivStarFive.setImageResource(R.mipmap.star_null);

        } else if (compare(commentRate, 3 + num2 * 6) + compare(commentRate, (3 + num2 * 7)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_full);
            ivStarThree.setImageResource(R.mipmap.star_full);
            ivStarFour.setImageResource(R.mipmap.star_null);
            ivStarFive.setImageResource(R.mipmap.star_null);

        } else if (compare(commentRate, 3 + num2 * 7) + compare(commentRate, (3 + num2 * 8)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_full);
            ivStarThree.setImageResource(R.mipmap.star_full);
            ivStarFour.setImageResource(R.mipmap.star_part);
            ivStarFive.setImageResource(R.mipmap.star_null);

        } else if (compare(commentRate, 3 + num2 * 8) + compare(commentRate, (3 + num2 * 9)) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_full);
            ivStarThree.setImageResource(R.mipmap.star_full);
            ivStarFour.setImageResource(R.mipmap.star_full);
            ivStarFive.setImageResource(R.mipmap.star_null);

        } else if (compare(commentRate, 3 + num2 * 9) + compare(commentRate, 3 + num2 * 10) == 0) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_full);
            ivStarThree.setImageResource(R.mipmap.star_full);
            ivStarFour.setImageResource(R.mipmap.star_full);
            ivStarFive.setImageResource(R.mipmap.star_part);
        } else if (compare(commentRate, 3 + num2 * 10) == 1) {
            ivStarOne.setImageResource(R.mipmap.star_full);
            ivStarTwo.setImageResource(R.mipmap.star_full);
            ivStarThree.setImageResource(R.mipmap.star_full);
            ivStarFour.setImageResource(R.mipmap.star_full);
            ivStarFive.setImageResource(R.mipmap.star_full);
        }

    }

}
