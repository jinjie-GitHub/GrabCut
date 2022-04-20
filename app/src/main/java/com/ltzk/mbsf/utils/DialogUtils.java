package com.ltzk.mbsf.utils;

import android.view.View;
import android.view.WindowManager;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

public class DialogUtils {

    public static void hideBottomNav(QMUIPopup dialog) {
        /*if (dialog.getDecorView() == null) {
            return;
        }
        hideBottomNavInner(dialog);
        dialog.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            hideBottomNavInner(dialog);
        });*/
    }

    private void updateDimAmount(QMUIPopup dialog) {
        View decorView = dialog.getDecorView();
        if (decorView != null) {
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) decorView.getLayoutParams();
            p.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //mWindowManager.updateViewLayout(decorView, p);
        }
    }

    private static void hideBottomNavInner(QMUIPopup dialog) {
        View decorView = dialog.getDecorView();
        int vis = decorView.getSystemUiVisibility();
        vis |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(vis);
    }
}