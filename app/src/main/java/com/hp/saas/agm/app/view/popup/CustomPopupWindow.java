package com.hp.saas.agm.app.view.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.widget.PopupWindow;
import com.hp.saas.agm.app.R;

/**
 * Created by lugan on 7/8/2014.
 */
public class CustomPopupWindow  extends PopupWindow {
    private Context context;
    private Dialog dialog;

    public CustomPopupWindow(Context context) {
        super();
        this.context = context;
        init();
    }

    public void init() {
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.app_pop);
        ColorDrawable dw = new ColorDrawable(Color.WHITE);
        this.setBackgroundDrawable(dw);

        dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        final View emptyDialog = LayoutInflater.from(context).inflate(R.layout.empty, null);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(emptyDialog);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnShowListener(new DialogInterface.OnShowListener(){
            @Override
            public void onShow(DialogInterface dialog){
                CustomPopupWindow.this.showAtLocation(emptyDialog.findViewById(R.id.rl_empty), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        this.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        this.dismiss();
    }


}
