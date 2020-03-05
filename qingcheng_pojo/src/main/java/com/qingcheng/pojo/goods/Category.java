package com.qingcheng.pojo.goods;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * category实体类
 * @author Administrator
 *
 */
@Table(name="tb_category")
public class Category implements Serializable{

	/**
	 *分类ID
	 * 如果实体类中没有一个标记 @Id 的字段，当你使用带有 ByPrimaryKey 的方法时，
	 * 所有的字段会作为联合主键来使用，也就会出现类似 where id = ? and countryname = ? and countrycode = ?的情况。
	 */
	@Id
	private Integer id;

	/**
	 *分类名称
	 */
	private String name;

	/**
	 *商品数量
	 */
	private Integer goodsNum;

	/**
	 *是否显示
	 */
	private String isShow;
	/**
	 *是否导航
	 */
	private String isMenu;

	/**
	 *排序
	 */
	private Integer seq;

	/**
	 *上级ID
	 */
	private Integer parentId;

	/**
	 *模板ID
	 */
	private Integer templateId;

	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(Integer goodsNum) {
		this.goodsNum = goodsNum;
	}

	public String getIsShow() {
		return isShow;
	}
	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public String getIsMenu() {
		return isMenu;
	}
	public void setIsMenu(String isMenu) {
		this.isMenu = isMenu;
	}

	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}


	
}
