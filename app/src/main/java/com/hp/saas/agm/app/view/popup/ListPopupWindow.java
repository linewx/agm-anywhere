package com.hp.saas.agm.app.view.popup;

import android.content.Context;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hp.saas.agm.app.R;
import com.hp.saas.agm.app.view.CustomListView;

/**
 * Created by lugan on 7/8/2014.
 */
public class ListPopupWindow extends CustomPopupWindow {
    private View listSelector;
    private CustomListView list;
    private TextView title;
    private PopupListener.ItemSelectedListener listener;
    private Object selected;


    public ListPopupWindow(Context context) {

        super(context);

        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listSelector = mLayoutInflater.inflate(
                R.layout.pop_list_selector, null);
        this.setContentView(listSelector);
        findView();


    }

    private void findView() {
        title = (TextView)listSelector.findViewById(R.id.selector_title);
        list = (CustomListView)listSelector.findViewById(R.id.selector_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Object newValue = parent.getAdapter().getItem(position);
                Object oldValue = selected;
                selected = newValue;
                fireSelected(newValue, oldValue);
                hide();
            }
        });
    }
    public void setAdapter(BaseAdapter adapter) {
        list.setAdapter(adapter);
    }

    public void setOnSelectedListener(PopupListener.ItemSelectedListener listener) {
        this.listener = listener;
    }

    private void fireSelected(Object newValue, Object oldValue) {
        this.listener.valueChanged(newValue, oldValue);
    }








    /*public void init() {
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
                ListPopupWindow.this.showAtLocation(emptyDialog.findViewById(R.id.rl_empty), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
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
    }*/


}
