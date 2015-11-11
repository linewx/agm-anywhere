package com.hp.saas.agm.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.hp.saas.agm.app.adapter.StatusAdapter;
import com.hp.saas.agm.app.adapter.TeamAdapter;
import com.hp.saas.agm.app.view.LoadingView;
import com.hp.saas.agm.app.view.popup.ListPopupWindow;
import com.hp.saas.agm.app.view.popup.PopupListener;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.manager.ApplicationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TaskDetailActivity extends Activity implements ActionBar.TabListener, OnClickListener {

    private Context mContext;


    private Entity task;
    private Entity originTask;
    private ActionBar actionBar;

    //value field
    private TextView tvStatus;
    private EditText etEstimated;
    private EditText etInvestied;
    private EditText etRemaining;
    private TextView tvAssignee;
    private EditText etDescription;


    //label view
    private TextView tvDescriptionLabel;
    private TextView tvStatusLabel;
    private TextView tvEstimatedLabel;
    private TextView tvInvestedLabel;
    private TextView tvRemainingLabel;
    private TextView tvAssigneeLabel;

    private LoadingView lvLoading;

    //click area
    private LinearLayout llDescription;
    private RelativeLayout rlStatus;
    private RelativeLayout rlEstimated;
    private RelativeLayout rlInvested;
    private RelativeLayout rlRemaining;
    private RelativeLayout rlAssignee;
    private LinearLayout llDummyFocus;

    private Button btnSave;

    private Set<String> changedField = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_activity);

        mContext = this;
        findView();
        init();

        actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void findView() {

        lvLoading = (LoadingView) findViewById(R.id.loading);
        llDummyFocus = (LinearLayout) findViewById(R.id.dummy_focus);

        //init field view
        etDescription = (EditText) findViewById(R.id.et_task_detail_description);
        tvStatus = (TextView) findViewById(R.id.tv_task_detail_status);
        etEstimated = (EditText) findViewById(R.id.et_task_detail_estimated);
        etInvestied = (EditText) findViewById(R.id.et_task_detail_invested);
        etRemaining = (EditText) findViewById(R.id.et_task_detail_remaining);
        tvAssignee = (TextView) findViewById(R.id.tv_task_detail_assignee);

        //init label
        tvDescriptionLabel = (TextView) findViewById(R.id.tv_task_detail_description_label);
        tvStatusLabel = (TextView) findViewById(R.id.tv_task_detail_status_label);
        tvEstimatedLabel = (TextView) findViewById(R.id.tv_task_detail_estimated_label);
        tvInvestedLabel = (TextView) findViewById(R.id.tv_task_detail_invested_label);
        tvRemainingLabel = (TextView) findViewById(R.id.tv_task_detail_remaining_label);
        tvAssigneeLabel = (TextView) findViewById(R.id.tv_task_detail_assignee_label);

        //init click area
        llDescription = (LinearLayout) findViewById(R.id.ll_task_detail_description);
        rlStatus = (RelativeLayout) findViewById(R.id.rl_task_detail_status);
        rlEstimated = (RelativeLayout) findViewById(R.id.rl_task_detail_estimated);
        rlInvested = (RelativeLayout) findViewById(R.id.rl_task_detail_invested);
        rlRemaining = (RelativeLayout) findViewById(R.id.rl_task_detail_remaining);
        rlAssignee = (RelativeLayout) findViewById(R.id.rl_task_detail_assignee);

        btnSave = (Button) findViewById(R.id.btn_task_detail_save);
    }

    private void init() {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        task = (Entity) data.getSerializable("task");
        task.setProperty("entity-type", "task");
        originTask = task.clone();

        initTask();

        etEstimated.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etEstimated.setSelection(etEstimated.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });

        etEstimated.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String entityProperty = "estimated";
                String entityValue = s.toString();

                boolean changed = envaluateChange(originTask, entityProperty, entityValue);
                updateEntity(task, entityProperty, entityValue, changedField, changed);
                updateLabel(changed, tvEstimatedLabel);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        etInvestied.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etInvestied.setSelection(etInvestied.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });

        etInvestied.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String entityProperty = "invested";
                String entityValue = s.toString();

                boolean changed = envaluateChange(originTask, entityProperty, entityValue);
                updateEntity(task, entityProperty, entityValue, changedField, changed);
                updateLabel(changed, tvInvestedLabel);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });



        etRemaining.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etRemaining.setSelection(etRemaining.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });

        etRemaining.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String entityProperty = "remaining";
                String entityValue = s.toString();

                boolean changed = envaluateChange(originTask, entityProperty, entityValue);
                updateEntity(task, entityProperty, entityValue, changedField, changed);
                updateLabel(changed, tvRemainingLabel);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        /*etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {

                    task.setProperty("description", etDescription.getText().toString());
                }
            }

        });*/

        etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etDescription.setSelection(etDescription.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String entityProperty = "description";
                String entityValue = s.toString();

                boolean changed = envaluateChange(originTask, entityProperty, entityValue);
                updateEntity(task, entityProperty, entityValue, changedField, changed);
                updateLabel(changed, tvDescriptionLabel);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        llDescription.setOnClickListener(taskItemClickListener);
        rlStatus.setOnClickListener(taskItemClickListener);
        rlEstimated.setOnClickListener(taskItemClickListener);
        rlInvested.setOnClickListener(taskItemClickListener);
        rlRemaining.setOnClickListener(taskItemClickListener);
        rlAssignee.setOnClickListener(taskItemClickListener);

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = save();
                if (result) {
                    reset();

                    ApplicationManager.getMessageService().show("save successfully");
                } else {
                    ApplicationManager.getMessageService().show("save failed, please try again.");
                }
            }
        });
    }

    private void initTask() {
        etDescription.setText(task.getPropertyValue("description"));
        tvStatus.setText(task.getPropertyValue("status"));
        etEstimated.setText(task.getPropertyValue("estimated"));
        etInvestied.setText(task.getPropertyValue("invested"));
        etRemaining.setText(task.getPropertyValue("remaining"));
        tvAssignee.setText(task.getPropertyValue("assigned-to"));
    }

    private boolean envaluateChange(Entity entity, String property, String value) {
        if (entity.getPropertyValue(property).equals(value)) {
            return false;
        } else {
            return true;
        }
    }

    private void enableButton() {
        btnSave.setEnabled(true);
        btnSave.setBackgroundColor(mContext.getResources().getColor(R.color.green));

    }

    private void disableButton() {
        btnSave.setEnabled(false);
        btnSave.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
    }

    @Override
    public void onClick(View v) {

    }

    private OnClickListener taskItemClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            llDummyFocus.requestFocus();
            if(v.getId() == R.id.ll_task_detail_description) {
                etDescription.requestFocus();
            }else if (v.getId() == R.id.rl_task_detail_status) {
                clickStatus(v);
            } else if (v.getId() == R.id.rl_task_detail_estimated) {
                etEstimated.requestFocus();
            } else if (v.getId() == R.id.rl_task_detail_invested) {
                etInvestied.requestFocus();
            } else if (v.getId() == R.id.rl_task_detail_remaining) {
                etRemaining.requestFocus();
            } else if (v.getId() == R.id.rl_task_detail_assignee) {
                clickAssignee(v);
            }

        }
    };


    private void clickStatus(View v) {
        try {
            ListPopupWindow popupWindow = new ListPopupWindow(mContext);
            popupWindow.setTitle("Status");
            ArrayList<String> statusList = new ArrayList<String>();
            statusList.add("New");
            statusList.add("In Progress");
            statusList.add("Completed");
            popupWindow.setAdapter(new StatusAdapter(mContext, statusList, task.getPropertyValue("status")));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                    String entityProperty = "status";
                    String entityValue = (String) newValue;
                    String ViewValue = entityValue;

                    boolean changed = envaluateChange(originTask, entityProperty, entityValue);
                    updateEntity(task, entityProperty, entityValue, changedField, changed);
                    updateLabel(changed, tvStatusLabel);
                    tvStatus.setText(entityValue);
                }
            });
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateEntity(Entity entity, String property, String value, Set<String> changedField, boolean changed) {
        if (changed) {
            changedField.add(property);
            entity.setProperty(property, value);
        } else {
            changedField.remove(property);
            entity.setProperty(property, value);
        }

        checkButtonStatus();

        //valueView.setText(value);
    }

    private void checkButtonStatus() {
        if (changedField.size() == 0) {
            disableButton();
        } else {
            enableButton();
        }
    }

    private void updateLabel(boolean changed, TextView labelView) {
        //boolean changed = envaluateChange(originReleaseBacklogItem, property, value);
        if (changed) {
            //highlight label
            String label = labelView.getText().toString();
            if (!label.endsWith("*")) {
                labelView.setText(label + "*");
            }
            labelView.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            String label = labelView.getText().toString();
            if (label.endsWith("*")) {
                labelView.setText(label.substring(0, label.length() - 1));
            }
            labelView.setTextColor(mContext.getResources().getColor(R.color.black));
        }

    }


    private void clickAssignee(View v) {
        try {
            ListPopupWindow popupWindow = new ListPopupWindow(mContext);
            popupWindow.setTitle("Assignee");
            Entity team = ApplicationManager.getSprintService().getTeam();
            EntityList teamMembers = ApplicationManager.getSprintService().getTeamMembers();
            popupWindow.setAdapter(new TeamAdapter(mContext, teamMembers, task.getPropertyValue("assignee")));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                    String entityProperty = "assignee";
                    String entityValue = ((Entity) newValue).getPropertyValue("name");
                    String ViewValue = entityValue;

                    boolean changed = envaluateChange(originTask, entityProperty, entityValue);
                    updateEntity(task, entityProperty, entityValue, changedField, changed);
                    updateLabel(changed, tvAssigneeLabel);
                    tvAssigneeLabel.setText(entityValue);

                }
            });
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /*private OnClickListener statusClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View statusSelector = mLayoutInflater.inflate(
                        R.layout.activity_story_detail, null);

                ListPopupWindow popupWindow = new ListPopupWindow(mContext);
                ArrayList<String> statusList = new ArrayList<String>();
                statusList.add("New");
                statusList.add("In Progress");
                statusList.add("In Testing");
                statusList.add("Done");
                popupWindow.setAdapter(new StatusAdapter(mContext, statusList, releaseBacklogItem.getPropertyValue("status")));
                popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                    @Override
                    public void valueChanged(Object newValue, Object oldValue) {
                        releaseBacklogItem.setProperty("status", newValue);
                        tvStatus.setText((String) newValue);
                    }
                });
                popupWindow.show();


            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
*/


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean save() {
        try {

            ApplicationManager.getEntityService().updateEntity(task, changedField, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public void reset() {
        //reset button status
        disableButton();

        //reset label
        restoreAllLabel();

        //reset origin entity
        restoreOriginEntity();
    }

    private void restoreAllLabel() {
        updateLabel(false, tvStatusLabel);
        updateLabel(false, tvEstimatedLabel);
        updateLabel(false, tvInvestedLabel);
        updateLabel(false, tvRemainingLabel);
        updateLabel(false, tvAssigneeLabel);
        updateLabel(false, tvDescriptionLabel);
    }

    public void restoreOriginEntity() {
        originTask = task.clone();
    }

}