package com.hp.saas.agm.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.hp.saas.agm.core.entity.EntityQuery;
import com.hp.saas.agm.core.entity.EntityRef;
import com.hp.saas.agm.manager.ApplicationManager;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.adapter.SprintAdapter;
import com.hp.saas.agm.app.adapter.StatusAdapter;
import com.hp.saas.agm.app.adapter.TeamAdapter;
import com.hp.saas.agm.app.view.LoadingView;

import com.hp.saas.agm.app.view.popup.ListPopupWindow;
import com.hp.saas.agm.app.view.popup.PopupListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StoryDetailActivity extends Activity implements ActionBar.TabListener, OnClickListener {

    //private CustomListView lvReleaseBacklog;
    //private LoadingView lvLoading;
    //private EntityList releaseBacklog = EntityList.empty();
    private Context mContext;
    private TextView tvRelease;
    private TextView tvSprint;
    private TextView tvStatus;
    private EditText tvPoint;
    private TextView tvName;
    private EditText etDescription;
    private TextView tvOwner;
    private TextView tvTask;
    private LinearLayout llStoryDetail;
    private LoadingView lvLoading;
    private Entity releaseBacklogItem;
    private Entity originReleaseBacklogItem;
    private Entity story;
    private Entity originStory;
    private ActionBar actionBar;


    //label view
    private TextView tvDescriptionLabel;
    private TextView tvReleaseLabel;
    private TextView tvSprintLabel;
    private TextView tvStatusLabel;
    private TextView tvPointLabel;
    private TextView tvOwnerLabel;
    private TextView tvTaskLabel;
    private TextView tvCommentLabel;


    //layout
    private LinearLayout llDescription;
    private RelativeLayout rlRelease;
    private RelativeLayout rlSprint;
    private RelativeLayout rlStatus;
    private RelativeLayout rlPoint;
    private RelativeLayout rlOwner;
    private RelativeLayout rlTask;
    private LinearLayout llDummyFocus;

    private Button btnSave;

    private Set<String> backlogChangedField = new HashSet<String>();
    private Set<String> storyChangedField = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity);

        mContext = this;
        findView();
        init();

        actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.story_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    private void findView() {
        llStoryDetail = (LinearLayout) findViewById(R.id.ll_story_detail);
        tvRelease = (TextView) findViewById(R.id.story_release);
        tvSprint = (TextView) findViewById(R.id.story_sprint);
        tvStatus = (TextView) findViewById(R.id.story_status);
        tvPoint = (EditText) findViewById(R.id.story_point);
        tvName = (TextView) findViewById(R.id.story_name);
        tvOwner = (TextView) findViewById(R.id.story_owner);
        etDescription = (EditText) findViewById(R.id.et_story_description);
        tvTask = (TextView) findViewById(R.id.story_task);
        lvLoading = (LoadingView) findViewById(R.id.loading);

        llDescription = (LinearLayout) findViewById(R.id.ll_story_description);
        rlRelease = (RelativeLayout) findViewById(R.id.rl_story_release);
        rlSprint = (RelativeLayout) findViewById(R.id.rl_story_sprint);
        rlStatus = (RelativeLayout) findViewById(R.id.rl_story_status);
        rlPoint = (RelativeLayout) findViewById(R.id.rl_story_point);
        rlOwner = (RelativeLayout) findViewById(R.id.rl_story_owner);
        rlTask = (RelativeLayout) findViewById(R.id.rl_story_task);

        llDummyFocus = (LinearLayout) findViewById(R.id.dummy_focus);

        tvDescriptionLabel = (TextView) findViewById(R.id.tv_story_description_label);
        tvReleaseLabel = (TextView) findViewById(R.id.label_story_release);
        tvSprintLabel = (TextView) findViewById(R.id.label_story_sprint);
        tvStatusLabel = (TextView) findViewById(R.id.label_story_status);
        tvPointLabel = (TextView) findViewById(R.id.label_story_point);
        tvOwnerLabel = (TextView) findViewById(R.id.label_story_owner);
        tvTaskLabel = (TextView) findViewById(R.id.label_story_task);

        btnSave = (Button) findViewById(R.id.btn_story_save);
    }

    private void init() {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        releaseBacklogItem = (Entity) data.getSerializable("release");
        originReleaseBacklogItem = releaseBacklogItem.clone();

        tvPoint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvPoint.setSelection(tvPoint.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });

        tvPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String entityProperty = "story-points";
                String entityValue = s.toString();

                boolean changed = envaluateChange(originReleaseBacklogItem, entityProperty, entityValue);
                updateEntity(releaseBacklogItem, entityProperty, entityValue, backlogChangedField, changed);
                updateLabel(changed, tvPointLabel);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String entityProperty = "description";
                String entityValue = s.toString();

                boolean changed = envaluateChangeWithHtml(originStory, entityProperty, entityValue);
                updateEntity(story, entityProperty, entityValue, storyChangedField, changed);
                updateLabel(changed, tvDescriptionLabel);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

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


        llDescription.setOnClickListener(storyClickListener);
        rlRelease.setOnClickListener(storyClickListener);
        rlSprint.setOnClickListener(storyClickListener);
        rlStatus.setOnClickListener(storyClickListener);
        rlPoint.setOnClickListener(storyClickListener);
        rlOwner.setOnClickListener(storyClickListener);
        rlTask.setOnClickListener(storyClickListener);

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

        llDummyFocus.requestFocus();
        new NewsAsyncTask(lvLoading).execute(0);
    }

    private boolean envaluateChange(Entity entity, String property, String value) {
        if (entity.getPropertyValue(property).equals(value)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean envaluateChangeWithHtml(Entity entity, String property, String value) {
        if ( Html.fromHtml(entity.getPropertyValue(property)).toString().equals(value) ) {
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

    private View.OnClickListener storyClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            llDummyFocus.requestFocus();
            if (v.getId() == R.id.ll_story_description) {
                etDescription.requestFocus();
            }else if (v.getId() == R.id.rl_story_task) {
                clickTask(v);
            } else if (v.getId() == R.id.rl_story_status) {
                clickStatus(v);
            } else if (v.getId() == R.id.rl_story_sprint) {
                clickSprint(v);
            } else if (v.getId() == R.id.rl_story_owner) {
                clickOwner(v);
            } else if (v.getId() == R.id.rl_story_point) {
                tvPoint.requestFocus();
/*                tvPoint.performClick();
                tvPoint.setPressed(true);*/
            }

        }
    };

    private void clickTask(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("releaseBacklogItem", releaseBacklogItem);
        bundle.putSerializable("story", story);
        Intent intent = new Intent(mContext, TaskActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        //finish();
    }

    private void clickStatus(View v) {
        try {
            ListPopupWindow popupWindow = new ListPopupWindow(mContext);
            popupWindow.setTitle("Status");
            ArrayList<String> statusList = new ArrayList<String>();
            statusList.add("New");
            statusList.add("In Progress");
            statusList.add("In Testing");
            statusList.add("Done");
            popupWindow.setAdapter(new StatusAdapter(mContext, statusList, releaseBacklogItem.getPropertyValue("status")));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                    String entityProperty = "status";
                    String entityValue = (String) newValue;
                    String ViewValue = entityValue;

                    boolean changed = envaluateChange(originReleaseBacklogItem, entityProperty, entityValue);
                    updateEntity(releaseBacklogItem, entityProperty, entityValue, backlogChangedField, changed);
                    updateLabel(changed, tvStatusLabel);
                    tvStatus.setText(entityValue);
                }
            });
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void clickSprint(View v) {
        try {
            ListPopupWindow popupWindow = new ListPopupWindow(mContext);
            popupWindow.setTitle("Sprint");
            popupWindow.setAdapter(new SprintAdapter(mContext, ApplicationManager.getSprintService().getSprints(), story.getPropertyValue("target-rcyc")));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                    String entityProperty = "target-rcyc";
                    String entityValue = ((Entity) newValue).getPropertyValue("id");
                    String viewValue = ((Entity) newValue).getPropertyValue("name");

                    boolean changed = envaluateChange(originStory, entityProperty, entityValue);
                    updateEntity(story, entityProperty, entityValue, storyChangedField, changed);
                    updateLabel(changed, tvSprintLabel);
                    tvSprint.setText(viewValue);
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
        if (backlogChangedField.size() == 0 && storyChangedField.size() == 0) {
            disableButton();
        } else {
            enableButton();
        }
    }

    private void updateLabel(boolean changed, TextView labelView) {
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

    private void restoreAllLabel() {
        updateLabel(false, tvSprintLabel);
        updateLabel(false, tvStatusLabel);
        updateLabel(false, tvPointLabel);
        updateLabel(false, tvOwnerLabel);
        updateLabel(false, etDescription);
    }

    public void restoreOriginEntity() {
        originReleaseBacklogItem = releaseBacklogItem.clone();
        originStory = story.clone();
    }


    private void clickOwner(View v) {
        try {
            ListPopupWindow popupWindow = new ListPopupWindow(mContext);
            popupWindow.setTitle("Owner");
            Entity team = ApplicationManager.getSprintService().getTeam();
            EntityList teamMembers = ApplicationManager.getSprintService().getTeamMembers();
            popupWindow.setAdapter(new TeamAdapter(mContext, teamMembers, releaseBacklogItem.getPropertyValue("owner")));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                    String entityProperty = "owner";
                    String entityValue = ((Entity) newValue).getPropertyValue("name");
                    String ViewValue = entityValue;

                    boolean changed = envaluateChange(originReleaseBacklogItem, entityProperty, entityValue);
                    updateEntity(releaseBacklogItem, entityProperty, entityValue, backlogChangedField, changed);
                    updateLabel(changed, tvOwnerLabel);
                    tvOwner.setText(entityValue);

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

    private Entity getStory() {
        EntityQuery query = new EntityQuery("requirement");
        query.addColumn("id", 1);
        query.addColumn("name", 1);
        query.addColumn("description", 1);
        //release
        query.addColumn("target-rel", 1);
        //sprint
        query.addColumn("target-rcyc", 1);
        query.addColumn("owner", 1);
        //
        query.setValue("id", String.valueOf(releaseBacklogItem.getPropertyValue("entity-id")));
        EntityList list = EntityList.empty();
        try {
            list = ApplicationManager.getEntityService().query(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private class NewsAsyncTask extends AsyncTaskBase {
        Entity recentStory = null;

        public NewsAsyncTask(LoadingView loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = 1;
            recentStory = getStory();
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            llStoryDetail.setVisibility(View.VISIBLE);
            story = recentStory;
            originStory = story.clone();

            //set releases
            String releaseId = story.getPropertyValue("target-rel");
            Entity releaseEntity = ApplicationManager.getSprintService().lookup(new EntityRef("release", Integer.parseInt(releaseId)));
            tvRelease.setText(releaseEntity.getPropertyValue("name"));

            //set sprints
            String sprintId = story.getPropertyValue("target-rcyc");
            Entity sprintEntity = ApplicationManager.getSprintService().lookup(new EntityRef("release-cycle", Integer.parseInt(sprintId)));
            tvSprint.setText(sprintEntity.getPropertyValue("name"));
            tvStatus.setText(releaseBacklogItem.getPropertyValue("status"));
            tvName.setText(story.getPropertyValue("name"));
            tvPoint.setText(releaseBacklogItem.getPropertyValue("story-points"));
            tvOwner.setText(releaseBacklogItem.getPropertyValue("owner"));
            etDescription.setText(Html.fromHtml(story.getPropertyValue("description")));


            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            llStoryDetail.setVisibility(View.INVISIBLE);
        }

    }


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
            case R.id.action_save:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean save() {
        try {
            if (backlogChangedField.size() != 0) {
                ApplicationManager.getEntityService().updateEntity(releaseBacklogItem, backlogChangedField, false, false, false);
            }

            if (storyChangedField.size() != 0) {
                ApplicationManager.getEntityService().updateEntity(story, storyChangedField, false, false, false);
            }

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

}