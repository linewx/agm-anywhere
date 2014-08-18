package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.hp.saas.agm.app.adapter.TeamAdapter;
import com.hp.saas.agm.app.view.LoadingView;
import com.hp.saas.agm.app.view.popup.PopupListener;
import com.hp.saas.agm.core.entity.EntityQuery;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.manager.ApplicationManager;

public class NewTaskActivity extends Activity implements OnClickListener {

    private Context mContext;
    private LoadingView lvLoading;
    private Button btnSave;
    private Button btnCancel;
    private EditText etDescription;
    private EditText etEstimated;
    private TextView tvBacklogName;
    private TextView tvAssginee;
    private RelativeLayout rlNewTaskEstimated;
    private RelativeLayout rlNewTaskAssginee;
    private LinearLayout llNewTask;
    private LinearLayout llNewTaskDescription;
    private Entity releaseBacklogItem;
    private LinearLayout llDummyFocus;
    private Entity story;
    private Entity task = new Entity("project-task");
    //private EntityList taskList = EntityList.empty();
    //private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task_activity);


        mContext = this;
        findView();
        init();
    }

    private void findView() {
        lvLoading = (LoadingView) findViewById(R.id.loading);
        btnSave = (Button) findViewById(R.id.btn_new_task_save);
        etDescription = (EditText) findViewById(R.id.et_new_task_description);
        etEstimated = (EditText) findViewById(R.id.et_new_task_estimated);
        tvBacklogName = (TextView) findViewById(R.id.tv_new_task_backlog_name);
        tvAssginee = (TextView) findViewById(R.id.tv_new_task_assignee);
        rlNewTaskAssginee = (RelativeLayout) findViewById(R.id.rl_new_task_assignee);
        rlNewTaskEstimated = (RelativeLayout) findViewById(R.id.rl_new_task_estimated);
        llNewTaskDescription = (LinearLayout) findViewById(R.id.ll_new_task_description);
        llNewTask = (LinearLayout) findViewById(R.id.ll_new_task);
        llDummyFocus = (LinearLayout) findViewById(R.id.dummy_focus);

    }

    private void init() {

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        //get release&story information
        releaseBacklogItem = (Entity)data.getSerializable("releaseBacklogItem");
        story = (Entity)data.getSerializable("story");

        //init task
        task.setProperty("release-backlog-item-id", releaseBacklogItem.getPropertyValue("id"));
        task.setProperty("status", "New");

        tvBacklogName.setText(releaseBacklogItem.getPropertyValue("entity-name"));
        llDummyFocus.requestFocus();
        rlNewTaskEstimated.setOnClickListener(newTaskClickListener);
        rlNewTaskAssginee.setOnClickListener(newTaskClickListener);
        llNewTaskDescription.setOnClickListener(newTaskClickListener);

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
                    //set estimation while losing focus
                    task.setProperty("estimated", etEstimated.getText().toString());
                    task.setProperty("invested", "0");
                    task.setProperty("remaining", etEstimated.getText().toString());
                }
            }

        });

        etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //etEstimated.setSelection(etEstimated.getText().length());
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    //set estimation while losing focus
                    task.setProperty("description", etDescription.getText().toString());
                }
            }

        });

        //bind save button
        btnSave.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                llDummyFocus.requestFocus();
                if( ApplicationManager.getEntityService().createEntity(task, true) == null) {
                    ApplicationManager.getMessageService().show("save failed!");
                }else {
                    ApplicationManager.getMessageService().show("save successfully.");
                    finish();
                }


            }
        });
    }

    private View.OnClickListener newTaskClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            llDummyFocus.requestFocus();
            if (v.getId() == R.id.ll_new_task_description) {
                etDescription.requestFocus();
            }else if (v.getId() == R.id.rl_new_task_assignee) {
                clickAssignee(v);
            } else if (v.getId() == R.id.rl_new_task_estimated) {
                etEstimated.requestFocus();
            }
        }
    };


    private void clickAssignee(View v) {
        try {
            com.hp.saas.agm.app.view.popup.ListPopupWindow popupWindow = new com.hp.saas.agm.app.view.popup.ListPopupWindow(mContext);
            popupWindow.setTitle("Assignee");
            Entity team = ApplicationManager.getSprintService().getTeam();
            EntityList teamMembers = ApplicationManager.getTeamMemberService().getTeamMembers(team);
            popupWindow.setAdapter(new TeamAdapter(mContext, teamMembers, releaseBacklogItem.getPropertyValue("owner")));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                    tvAssginee.setText((String) ((Entity) newValue).getPropertyValue("name"));
                    task.setProperty("assigned-to", ((Entity) newValue).getPropertyValue("name"));
                }
            });
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addNewTask(Entity task) {
        ApplicationManager.getEntityService().createEntity(task, true);
    }

    @Override
    public void onClick(View v) {

    }

    private OnClickListener taskClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NewTaskActivity.class);
            startActivity(intent);
            //finish();
        }
    };

    private EntityList getProjectTask() {
        EntityQuery query = new EntityQuery("project-task");
        query.addColumn("id", 1);
        query.addColumn("status", 1);
        query.addColumn("description", 1);
        query.addColumn("estimated", 1);
        query.addColumn("invested", 1);
        query.addColumn("remaining",1);
        query.addColumn("assigned-to", 1);
        query.addColumn("release-backlog-item-id", 1);
        query.setValue("release-backlog-item-id", String.valueOf(releaseBacklogItem.getPropertyValue("id")));
        EntityList list = EntityList.empty();
        try {
            list = ApplicationManager.getEntityService().query(query);
        } catch(Exception e) {
            e.printStackTrace();
        }finally {

        }

        if (list.size() > 0) {
            return list;
        }else {
            return null;
        }
    }







}