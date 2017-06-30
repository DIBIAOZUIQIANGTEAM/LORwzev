package com.wsns.lor.http.entity;

import java.io.Serializable;

/*
 * 订单评价表
 */
public class OrdersComment implements Serializable {

	private int id;
	private Orders orders;
	private String comments;// 买家评论
	private String reply;// 商家回复
	private String createDate;
	private String editDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getEditDate() {
		return editDate;
	}

	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}

	@Override
	public String toString() {
		return "OrdersComment [orders=" + orders + ", comments=" + comments + ", reply=" + reply + "]";
	}

}
