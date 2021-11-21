package org.susi.integration.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Shop {

    private String shopId;
    private String shopName;

    public Shop() {
    }


    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
