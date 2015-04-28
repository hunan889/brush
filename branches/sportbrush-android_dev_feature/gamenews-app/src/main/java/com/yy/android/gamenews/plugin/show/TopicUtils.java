package com.yy.android.gamenews.plugin.show;

import java.util.Map;

import com.duowan.show.Image;
import com.duowan.show.PicInfo;
import com.duowan.show.Tag;
import com.duowan.show.Topic;

public class TopicUtils {

	public static PicInfo getImageFromTopic(Topic topic, int type) {
		if (topic == null) {
			return null;
		}

		Image image = topic.getImage();
		return getImageFromImage(image, type);
	}
	
	public static PicInfo getImageFromTag(Tag tag, int type) {
		if (tag == null) {
			return null;
		}

		Image image = tag.getIconList();
		return getImageFromImage(image, type);
	}
	
	public static PicInfo getImageFromImage(Image image, int type) {
		if (image == null) {
			return null;
		}

		Map<Integer, PicInfo> map = image.getImage();
		if (map == null) {
			return null;
		}

		PicInfo info = map.get(type);
		return info;
	}
}
