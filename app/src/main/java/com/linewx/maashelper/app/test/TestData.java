package com.linewx.maashelper.app.test;

import com.linewx.maashelper.app.bean.RecentChat;
import com.linewx.maashelper.app.utils.FileUtil;
import com.hp.alm.ali.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestData {
	private static String[] names={"songhuiqiao.jpg","zhangzetian.jpg","songqian.jpg","hangxiaozhu.jpg","jingtian.jpg"
		,"liuyifei.jpg","kangyikun.jpg","dengziqi.jpg"};
	private static String dir=FileUtil.getRecentChatPath();
	public static List<RecentChat> getRecentChats(){
		String path=FileUtil.getRecentChatPath();
		List<RecentChat> chats=new ArrayList<RecentChat>();
		chats.add(new RecentChat("宋慧乔", "好好学习天天向上", "12:30", path+names[0]));
		chats.add(new RecentChat("章泽天", "好好学习天天向上", "16:30", path+names[1]));
		chats.add(new RecentChat("宋茜", "好好学习天天向上", "17:30", path+names[2]));
		chats.add(new RecentChat("韩孝珠", "好好学习天天向上", "昨天", path+names[3]));
		chats.add(new RecentChat("景甜", "好好学习天天向上", "星期一", path+names[4]));
		chats.add(new RecentChat("刘亦菲", "好好学习天天向上", "17:30", path+names[5]));
		chats.add(new RecentChat("康逸琨", "好好学习天天向上", "昨天", path+names[6]));
		chats.add(new RecentChat("邓紫棋", "好好学习天天向上", "星期一", path+names[7]));
		return chats;
	}
	
	public static HashMap<String, String> getFriends(){
		HashMap<String, String> maps=new HashMap<String, String>();
		maps.put("宋慧乔", dir+"songhuiqiao.jpg");
		maps.put("章泽天", dir+"zhangzetian.jpg");
		maps.put("宋茜", dir+"songqian.jpg");
		maps.put("韩孝珠", dir+"hangxiaozhu.jpg");
		maps.put("景甜", dir+"jingtian.jpg");
		maps.put("刘亦菲", dir+"liuyifei.jpg");
		maps.put("康逸琨", dir+"kangyikun.jpg");
		maps.put("邓紫棋", dir+"dengziqi.jpg");
		
		return maps;
	}

    public static List<Entity> getStories(){
        List<Entity> stories = new ArrayList<Entity>();
        Entity story1 = new Entity("story");
        story1.setProperty("Name", "As a contract mgr, I want to see vendor/brand grid in one line tall");
        story1.setProperty("Status", "New");
        story1.setProperty("Owner", "guest@test.com");

        stories.add(story1);

        return stories;
    }
}
