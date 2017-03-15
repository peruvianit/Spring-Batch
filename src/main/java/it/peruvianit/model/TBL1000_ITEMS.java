package it.peruvianit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tbl1000_items")
public class TBL1000_ITEMS {

	@Id
	@Column(name="PRG_ITEM")
	private Integer prg_item;
	
	@Column(name="DESC_ITEM")
	private String desc_item;

	public Integer getPrg_item() {
		return prg_item;
	}

	public void setPrg_item(Integer prg_item) {
		this.prg_item = prg_item;
	}

	public String getDesc_item() {
		return desc_item;
	}

	public void setDesc_item(String desc_item) {
		this.desc_item = desc_item;
	}
}
