package com.wsns.lor.entity;



import java.io.Serializable;

public class RepairGoods implements Serializable {
	int id;
	User seller;
	String brand;//品牌商
	String type;//型号

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getSeller() {
		return seller;
	}
	public void setSeller(User seller) {
		this.seller = seller;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
