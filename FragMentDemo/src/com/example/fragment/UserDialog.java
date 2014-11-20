package com.example.fragment;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fragmentdemo.R;

public class UserDialog {
    private static IdialogClickResult iClickResult;
    private static EditText userName;
    private static AlertDialog.Builder builder;
    private static AlertDialog mDialog;
    private static Context context;

    /**
     * 带编辑框的对话框
     */
    public static void isSaveChangeDialog(Context context, IdialogClickResult res, String title, String subTile) {

        iClickResult = res;
        UserDialog.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.user_dialog, null);

        builder = new AlertDialog.Builder(context);
        userName = (EditText) dialogView.findViewById(R.id.user_name);
        TextView txtViewSubTitle = (TextView) dialogView.findViewById(R.id.tip_text);
        txtViewSubTitle.setText(subTile);
        // 不关闭写�?
        builder.setCancelable(false);

        builder.setView(dialogView);
        builder.setTitle(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (userName.getText() == null || userName.getText().length() == 0) {
                    finishDialog(false, mDialog);

                    return;
                }
                iClickResult.clickOnYes(userName.getText().toString());
                finishDialog(true, mDialog);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                iClickResult.clickOnNo();
                finishDialog(true, mDialog);
            }
        });
        mDialog = builder.create();
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        mDialog.show();
    }

    private static void finishDialog(boolean isFinish, AlertDialog dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            // 设置mShowing值，欺骗android系统
            field.set(dialog, isFinish);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract interface IdialogClickResult {
        public abstract void clickOnYes(String userName);

        public abstract void clickOnNo();
    }
}
