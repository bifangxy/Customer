package com.xiaopu.customer.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;

/**
 * 创建时间:2015年06月24日 下午2:23</br>
 * 功能描述:</br>
 * 注意事项:</br>
 * ------------1.本类由lucifd开发,阅读、修改时请勿随意修改代码排版格式后提交到版本控制器。本人有十分严重的代码洁癖!</br>
 * ------------2.本类仅供阅读使用,如需修改此类可将此类继承后对子类进行操作.</br>
 * ------------3.如需在本类内部进行修改,请先联系lucifd,若未经同意修改此类后造成损失本人概不负责</br>
 *
 * @author lucifd@sina.cn
 */
public class ToastUtils {
    /**
     * 类标签
     */
    public static final String LOG_TAG = "ToastUtils";

    public static void show(int res, String msg) {
        View rootView = LayoutInflater.from(ApplicationXpClient.getInstance()).inflate(R.layout.item_toast_stytle, null);
        ImageView iconImg = (ImageView) rootView.findViewById(R.id.iconImg);
        TextView messageTex = (TextView) rootView.findViewById(R.id.messageTex);
        iconImg.setImageResource(res);
        messageTex.setText(msg);

        Toast toast = new Toast(ApplicationXpClient.getInstance());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(rootView);

        toast.show();
    }

    public static void showPush(int res, String msg) {
        View rootView = LayoutInflater.from(ApplicationXpClient.getInstance()).inflate(R.layout.push_toast_stytle, null);
        ImageView iconImg = (ImageView) rootView.findViewById(R.id.iconImg);

        TextView messageTex = (TextView) rootView.findViewById(R.id.messageTex);
        iconImg.setImageResource(res);
        messageTex.setText(msg);

        Toast toast = new Toast(ApplicationXpClient.getInstance());
        toast.setDuration(Toast.LENGTH_LONG);
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(rootView);
        toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);

        toast.show();
    }

    public static void showCorrectMsg(String msg) {
        show(R.mipmap.tubiaoduihao, msg);
    }

    public static void showErrorMsg(String msg) {
        show(R.mipmap.tubiaocha, msg);
    }

    public static void showWarnMsg(String msg) {
        show(R.mipmap.tubiaotixing, msg);
    }

    public static void showPushMsg(String msg) {
        showPush(R.mipmap.tubiaotixing, msg);
    }
}
