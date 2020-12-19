package dto;

import annotation.Column;
import annotation.Entity;
import annotation.Id;
import annotation.Table;

@Entity
@Table(name = "product")
public class ProductEntity {
	
	@Id
	@Column(name = "product_id")
	private Integer product_id;
	
	@Column(name = "product_name")
	private String product_name;
	
	@Column(name = "product_price")
	private Integer product_price;

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public Integer getProduct_price() {
		return product_price;
	}

	public void setProduct_price(Integer product_price) {
		this.product_price = product_price;
	}

	public ProductEntity(String product_name, Integer product_price) {
		super();
		this.product_name = product_name;
		this.product_price = product_price;
	}
		
}
