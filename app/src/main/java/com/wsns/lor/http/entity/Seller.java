package com.wsns.lor.http.entity;


import java.io.Serializable;

/**
 * 商家实体类
 * @author JR
 *
 */
public class Seller implements Serializable {
	private int id;
	private String account;
	private String passwordHash;
	private String avatar;//头像本地相对地址
	private String name;// 店名
	private Double coin;//余额
	private String email;//邮箱，暂时没用到
	private String address;// 商家地址
	private String hotline;// 热线电话
	private Double service;// 人工服务费（每次交易最少收取费用）
	private String worktime;// 营业时间
	private String notice;// 商家公告
	private Integer turnover;// 交易量
	private String repairsTypes;// 维修类型：如空调，电脑
	private String state;// 店铺状态 1在线状态 ,2离线状态
	private String createDate;
	private String distance;// 距离（不在数据库插入此字段）
	private Integer comment;// 评价数
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getName() {
		return name;
	}

	public Double getCoin() {
		return coin;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}

	public String getHotline() {
		return hotline;
	}

	public Double getService() {
		return service;
	}

	public String getWorktime() {
		return worktime;
	}

	public String getNotice() {
		return notice;
	}

	public Integer getTurnover() {
		return turnover;
	}


	public String getRepairsTypes() {
		return repairsTypes;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setHotline(String hotline) {
		this.hotline = hotline;
	}

	public void setService(Double service) {
		this.service = service;
	}

	public void setWorktime(String worktime) {
		this.worktime = worktime;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public void setTurnover(Integer turnover) {
		this.turnover = turnover;
	}



	public void setRepairsTypes(String repairsTypes) {
		this.repairsTypes = repairsTypes;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCoin(Double coin) {
		this.coin = coin;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public Integer getComment() {
		return comment;
	}

	public void setComment(Integer comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Seller [account=" + account + ", passwordHash=" + passwordHash + ", avatar=" + avatar + ", name=" + name
				+ ", coin=" + coin + ", email=" + email + ", address=" + address + ", hotline=" + hotline + ", service="
				+ service + ", worktime=" + worktime + ", notice=" + notice + ", turnover=" + turnover
				 + ", repairsTypes=" + repairsTypes + "]";
	}

}
