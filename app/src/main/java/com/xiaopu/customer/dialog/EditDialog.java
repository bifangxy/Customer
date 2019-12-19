package com.xiaopu.customer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.utils.T;

/**
 * Created by Administrator on 2017/6/21.
 */

public class EditDialog extends Dialog {
    public EditDialog(@NonNull Context context) {
        super(context);
    }

    public EditDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected EditDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static final class Builder {
        private Context mContext;

        private View contentView;

        private String name;

        private String title;

        private OnSureButtonClickListener mListener;

        public Builder(Context mContext, String name, String title) {
            this.mContext = mContext;
            this.name = name;
            this.title = title;
        }

        public void setOnSureButtonClickListener(OnSureButtonClickListener listener) {
            this.mListener = listener;
        }

        public EditDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final EditDialog dialog = new EditDialog(mContext, R.style.Dialog);

            View layout = inflater.inflate(R.layout.edit_dialog, null);

            final EditText et_device_nickname = (EditText) layout.findViewById(R.id.et_device_nickname);
            TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
            Button bt_ensure = (Button) layout.findViewById(R.id.bt_ensure);

            tv_title.setText(title);

            et_device_nickname.setText(name);

            et_device_nickname.setSelection(et_device_nickname.getText().length());

            bt_ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (et_device_nickname.getText().toString().length() == 0) {
                        T.showShort("输入不能为空");
                    } else {
                        dialog.dismiss();
                        mListener.click(et_device_nickname.getText().toString());
                    }
                }
            });

            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            return dialog;
        }


        public interface OnSureButtonClickListener {
            void click(String string);
        }
    }
}
