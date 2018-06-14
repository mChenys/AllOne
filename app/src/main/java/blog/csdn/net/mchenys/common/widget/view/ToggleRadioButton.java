package blog.csdn.net.mchenys.common.widget.view;

import android.content.Context;
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
}
