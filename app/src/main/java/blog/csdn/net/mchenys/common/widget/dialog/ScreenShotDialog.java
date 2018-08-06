package blog.csdn.net.mchenys.common.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.common.utils.BitmapUtils;
import blog.csdn.net.mchenys.common.utils.DisplayUtils;
import blog.csdn.net.mchenys.common.utils.StringUtils;

/**
 * 截屏弹窗
 * Created by mChenys on 2018/7/26.
 */

public class ScreenShotDialog extends Dialog {

    private ImageView mCoverIv;

    public ScreenShotDialog(@NonNull Context context) {
        this(context, R.style.custom_dialog);
    }

    public ScreenShotDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_screen_shot);
        mCoverIv = findViewById(R.id.iv_shot_cover);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
     //   dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 悬浮弹窗
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        lp.y = DisplayUtils.dip2px(getContext(), 20);
        dialogWindow.setAttributes(lp);
    }

    public void show(String path) {
        Activity activity = scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (!isShowing() && !StringUtils.isEmpty(path)) {

            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(path, 0,0);
            mCoverIv.setImageBitmap(bitmap);
            show();
           /* if (Build.VERSION.SDK_INT >= 23 &&!Settings.canDrawOverlays(activity)) {
                final Activity finalActivity = activity;
                new AlertDialog.Builder(activity)
                .setTitle("检测到你没有开启悬浮弹窗,是否开启")
                .setPositiveButton("是", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        finalActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("否",null).show();
            }else{
                show();
            }*/

        }

    }

    private static Activity scanForActivity(Context ctx) {
        if (ctx == null)
            return null;
        else if (ctx instanceof Activity)
            return (Activity)ctx;
        else if (ctx instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)ctx).getBaseContext());

        return null;
    }

}
