package com.wsns.lor.http.entity;

import java.io.Serializable;

/**
 * 三张广告图的路径
 *
 */
public class PublishAd implements Serializable {
	private int id;
	private String des;
	private String img;
	private String link;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
