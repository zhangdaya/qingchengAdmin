package com.qingcheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * album实体类
 *
 * @author Administrator
 */
@Table(name = "tb_album")
public class Album implements Serializable {

    /**
     * 编号
     */
    @Id
    private Long id;

    /**
     * 相册名称
     */
    private String title;

    /**
     * 相册封面
     */
    private String image;

    /**
     * 图片列表
     */
    private String imageItems;

    /**
     * 商品数量
     */
    private Integer imageNum;

    /**
     * 排序
     */
    private Integer seq;

    /**
     * 描述
     */
    private String imageDesc;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageItems() {
        return imageItems;
    }

    public void setImageItems(String imageItems) {
        this.imageItems = imageItems;
    }

    public Integer getImageNum() {
        return imageNum;
    }

    public void setImageNum(Integer imageNum) {
        this.imageNum = imageNum;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getImageDesc() {
        return imageDesc;
    }

    public void setImageDesc(String imageDesc) {
        this.imageDesc = imageDesc;
    }
}
