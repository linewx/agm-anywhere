package com.linewx.maashelper.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hp.alm.ali.model.Entity;
import com.hp.alm.ali.model.parser.EntityList;
import com.linewx.maashelper.app.R;
import com.linewx.maashelper.app.view.CustomListView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

public class ReleaseBacklogAdapter extends BaseAdapter {
	protected static final String TAG = "ReleaseBacklogAdapter";
	private Context mContext;
	private List<Entity> lists;
	private CustomListView mCustomListView;
	private HashMap<String, SoftReference<Bitmap>> hashMaps = new HashMap<String, SoftReference<Bitmap>>();

	public ReleaseBacklogAdapter(Context context, EntityList lists,
                                 CustomListView customListView) {
		this.mContext = context;
		this.lists = lists;
		this.mCustomListView = customListView;
	}

	@Override
	public int getCount() {
		if (lists != null) {
			return lists.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		Entity story = lists.get(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.fragment_story_item,
                    null);
			holder = new Holder();
			holder.tv_status = (TextView) convertView.findViewById(R.id.story_status);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.story_name);
			holder.tv_owner = (TextView) convertView
					.findViewById(R.id.story_owner);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		/*String path = chat.getImgPath();
		if (hashMaps.containsKey(path)) {
            holder.iv.setImageBitmap(hashMaps.get(path).get());
            //另一个地方缓存释放资源
			ImgUtil.getInstance().reomoveCache(path);
		} else {
			holder.iv.setTag(chat.getImgPath());
			ImgUtil.getInstance().loadBitmap(chat.getImgPath(),
					new OnLoadBitmapListener() {
						@Override
						public void loadImage(Bitmap bitmap, String path) {
							ImageView iv = (ImageView) mCustomListView
									.findViewWithTag(path);
							if (bitmap != null && iv != null) {
								bitmap = SystemMethod.toRoundCorner(bitmap, 15);
								iv.setImageBitmap(bitmap);

								if (!hashMaps.containsKey(path)) {
									hashMaps.put(path,
											new SoftReference<Bitmap>(bitmap));
								}
							}
						}
					});

		}*/

        holder.tv_status.setText(story.getPropertyValue("status"));
		holder.tv_name.setText(story.getPropertyValue("entity-name"));
		holder.tv_owner.setText(story.getPropertyValue("owner"));
		return convertView;
	}

	class Holder {
		TextView tv_status;
		TextView tv_name;
		TextView tv_owner;
	}

}
