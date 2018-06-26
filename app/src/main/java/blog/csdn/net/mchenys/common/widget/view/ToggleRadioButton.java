package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * 自定义可取消radioButton，可与radioGroup一起用,clearCheck可以重置RadioButton的状态
 * Created by mChenys on 2018/6/11.
 */

public class ToggleRadioButton extends android.support.v7.widget.AppCompatRadioButton {

    public ToggleRadioButton(Context context) {
        super(context);
    }

    public ToggleRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
        if (!isChecked()) {
            if (null != getParent() && getParent()  instanceof RadioGroup)
                ((RadioGroup) getParent()).clearCheck();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //得到Drawable集合  分别对应 左上右下
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            //获取右边图片,修改drawableRight的图片紧贴着文字
            Drawable drawableRight = drawables[2];
            if (drawableRight != null) {
                //获取文字占用长宽
                int textWidth = (int) getPaint().measureText(getText().toString());
                int textHeight = (int) getPaint().getTextSize();
                //获取图片实际长宽
                int drawableWidth = drawableRight.getIntrinsicWidth();
                int drawableHeight = drawableRight.getIntrinsicHeight();
                //setBounds修改Drawable在View所占的位置和大小,对应参数同样的 左上右下()
                int bodyWidth = textWidth + drawableWidth + getCompoundDrawablePadding();
                int left = (bodyWidth - getWidth()) / 2;
                int right = left + drawableWidth;
                drawableRight.setBounds(left, 0, right, drawableHeight);
            }
        }
        super.onDraw(canvas);
    }
}
