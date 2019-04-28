package blog.csdn.net.mchenys.module.terminal;

import blog.csdn.net.mchenys.common.base.BaseTerminalActivity;
import blog.csdn.net.mchenys.common.config.Constant;
import blog.csdn.net.mchenys.common.config.Urls;

/**
 * Created by mChenys on 2019/4/19.
 */

public class ProductActivity extends BaseTerminalActivity {

    @Override
    protected void initData() {
        super.initData();
        String skuId = getIntent().getStringExtra(Constant.KEY_ID);
        url = Urls.PRODUCT_DETAIL + "?skuId" + skuId;
    }
}
