package blog.csdn.net.mchenys.common.utils;

/**
 *  Created by mChenys on 2017/12/27.
 */
public class GetPageTotalUtils {
    /**
     * 根据总数和每页显示的数目计算页数
     *
     * @param total
     * @param pageSize
     * @return
     */
    public static int getPageTotal(int total, int pageSize) {
        if (pageSize == 0 || total == 0) return 0;
        int pageTotal;
        if (total % pageSize == 0) {
            pageTotal = total / pageSize;
        } else {
            pageTotal = total / pageSize + 1;
        }
        return pageTotal;
    }
}
