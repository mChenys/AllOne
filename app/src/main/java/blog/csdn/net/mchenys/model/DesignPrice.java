package blog.csdn.net.mchenys.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 设计报价
 * Created by mChenys on 2018/6/11.
 */

public class DesignPrice {
    public int priceType;
    public String price;
    public boolean isSelected;

    public DesignPrice(int priceType, String price, boolean isSelected) {
        this.priceType = priceType;
        this.price = price;
        this.isSelected = isSelected;
    }

    public static List<DesignPrice> getAllPrice() {
        List<DesignPrice> list = new ArrayList<>();
        list.add(new DesignPrice(0, "全部报价", true));
        list.add(new DesignPrice(1, "100元/m²以下", false));
        list.add(new DesignPrice(2, "100-199元/m²", false));
        list.add(new DesignPrice(3, "200-299元/m²", false));
        list.add(new DesignPrice(4, "300-500元/m²", false));
        list.add(new DesignPrice(5, "500元/m²以上", false));
        return list;
    }

}
