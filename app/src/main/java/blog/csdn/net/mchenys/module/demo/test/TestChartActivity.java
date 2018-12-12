package blog.csdn.net.mchenys.module.demo.test;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointD;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import blog.csdn.net.mchenys.R;
import blog.csdn.net.mchenys.module.demo.mpandroidchart.MyMarkerView;

/**
 * Created by mChenys on 2018/10/31.
 * https://blog.csdn.net/u014136472/article/details/50273309
 */

public class TestChartActivity extends AppCompatActivity {
    private LineChart mLineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chart);
        initView();
        initListener();
    }

    private void initView() {
        mLineChart = findViewById(R.id.chart);
        //1.设置x轴和y轴的点
        List<Entry> entries = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            entries.add(new Entry(i, new Random().nextInt(300)));
        }
        setData(entries);
    }

    private void initListener() {
        //设置数值选择监听
        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.e("cys", "x:" + e.getX() + " y:" + e.getY());

                //获取每个点对应在折线图上的xy像素
                LineDataSet set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
                MPPointD pixels = mLineChart.getTransformer(
                        set1.getAxisDependency()).getPixelForValues(e.getX(), e.getY());
                Log.e("cys", "xpx:" + pixels.x + " ypx:" + pixels.y+" h.x:"+h.getXPx()+" h.y:"+h.getYPx());
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    /**
     * 通过数据生成折线图
     *
     * @param entries
     */
    public void setData(List<Entry> entries) {
        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            //每一个对象就是一条连接线,假设只有一条连接线
            LineDataSet set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(entries);
            //刷新数据
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            //--------------------基本操作也就3步---------------------
            //1.创建一条线
            LineDataSet set1 = new LineDataSet(entries, "测试数据1");
            //2.设置线条样式
            set1.setColor(Color.RED);//线条颜色
            set1.setLineWidth(2f);//线条宽度
            //是否绘制圆的
            set1.setDrawCircles(false);
            //如果禁用了,下面的属性就不生效了
//            set1.setCircleColor(Color.RED);//圆点颜色
//            set1.setCircleRadius(3f);//设置焦点圆心的大小

            //是否禁用点击显示定位线,注意当这个属性设为false时，MarkerView不显示
            set1.setHighlightEnabled(true);
            //如果禁用以下的设置全部不生效
//        set1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
//        set1.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
//        set1.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
//        set1.setValueTextSize(9f);//设置显示值的文字大小
            //如果不想显示定位线,但是又想显示MarkerView，可以将定位线的颜色设为透明
            set1.setHighLightColor(Color.TRANSPARENT);

            //设置是否禁用范围背景填充
            set1.setDrawFilled(false);
            //如果禁用以下的设置全部不生效
//        if (Utils.getSDKInt() >= 18) {
//            // fill drawable only supported on api level 18 and above
//            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
//            set1.setFillDrawable(drawable);//设置范围背景填充
//        } else {
//            set1.setFillColor(Color.BLACK);
//        }
            // 是否在点上绘制Value
            set1.setDrawValues(false);
            //如果禁用以下的设置全部不生效
//            set1.setValueTextColor(Color.RED);//绘制value的颜色
//            set1.setValueTextSize(12f);//大小
//            set1.setValueFormatter(new IValueFormatter() {
//                @Override
//                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                    return value+"元";
//                }
//            });
            //2点间连线的方式是尖角(Mode.LINEAR)还是矩形(Mode.STEPPED),还是立方(Mode.CUBIC_BEZIER),横向立方(Mode.HORIZONTAL_BEZIER)
            set1.setMode(LineDataSet.Mode.STEPPED);//默认尖角Mode.LINEAR

            //3.将数据添加到图表
            //保存LineDataSet集合,假设有多条线,即会有多个LineDataSet
            ArrayList<ILineDataSet> list = new ArrayList<>();
            list.add(set1); // 可以添加多个LineDataSet,每一个LineDataSet代表一条线的数据
            //创建LineData对象 属于LineChart折线图的数据集合
            LineData data = new LineData(list);//有2个构造方法,如果是单条先可以用另一个构造方法直接传入一个LineDataSet
            // 添加到图表中
            mLineChart.setData(data);


            //---------------------下面是更加细腻的设置------------------------------

            //4.设置x和y轴样式(方法名基本一致)

            //设置左右两边的y轴
            YAxis rightAxis = mLineChart.getAxisRight();//获取右边y轴
            rightAxis.setEnabled(false);//设置图表右边的y轴禁用,默认开启
            YAxis leftAxis = mLineChart.getAxisLeft(); //获取左边y轴
            leftAxis.setEnabled(true);//开启左边y轴,默认开启
            leftAxis.setDrawLabels(true);//绘制y轴标签  指y轴上的对应数值,默认开启
            leftAxis.setDrawAxisLine(true);//是否绘制轴线
            leftAxis.setDrawGridLines(true);//开启绘制y轴上每个点对应的横线,默认是实线,默认开启
            leftAxis.setGridLineWidth(1f);//设置y轴上的点对应的横线大小
            leftAxis.setTextColor(Color.parseColor("#cecece"));//设置y轴字体颜色
            leftAxis.setGridColor(Color.parseColor("#cecece"));//设置y轴颜色
            leftAxis.setAxisMinimum(50);//y轴最小值
            leftAxis.setAxisMaximum(300);//y轴最大值
            //leftAxis.setInverted(true);//是否要将轴反转,默认false,即从小到大显示
            // 设置图表中的最高值的顶部间距占最高值的值的百分比（设置的百分比 = 最高柱顶部间距/最高柱的值）。默认值是10f，即10% 。
            //leftAxis.setSpaceTop(100f);//间距长度是值长度的100%，即两者相等
            leftAxis.setLabelCount(3);//设置y轴的标签数量


            //设置x轴
            XAxis xAxis = mLineChart.getXAxis();
            xAxis.setEnabled(true);//设置轴启用或禁用 如果禁用以下的设置全部不生效
            xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值,默认开启
            xAxis.setDrawAxisLine(true);//是否绘制轴线
            xAxis.setDrawGridLines(true);//开启绘制x轴上每个点对应的竖直线,默认是实线,默认关闭
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
            xAxis.setTextSize(11f);//设置字体
            xAxis.setGridLineWidth(1f);//设置竖线大小
            xAxis.setTextColor(Color.parseColor("#cecece"));//x轴字体颜色
            xAxis.setGridColor(Color.parseColor("#cecece"));//设置竖线颜色
            //设置字体
            Typeface tf1 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
            xAxis.setTypeface(tf1);
            //设置竖线的显示样式为虚线,默认是实线
            //参数1:lineLength控制虚线段的长度
            //参数2:spaceLength控制线之间的空间
            //xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setAxisMinimum(0f);//设置x轴的最小值
            xAxis.setAxisMaximum(11f);//设置x轴最大值
            xAxis.setAvoidFirstLastClipping(true);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
            xAxis.setLabelRotationAngle(10f);//设置x轴标签的旋转角度
            xAxis.setGranularity(1f);//禁止放大后x轴标签重绘

            //设置x轴显示标签数量  还有一个重载方法第二个参数为布尔值强制设置数量 如果启用会导致绘制点出现偏差
            xAxis.setLabelCount(12);//若不设置则不会完全显示出所有标签数值
            //格式化x轴
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.valueOf((int) value + 1).concat("月");
                }
            });
            //格式化y轴标签
            leftAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.valueOf((int) value).concat("元");
                }
            });

            //5.添加限制线
            // 设置x轴的LimitLine，index是从0开始的
            LimitLine xLimitLine = new LimitLine(5f, "xL 测试");
            xLimitLine.setLineColor(Color.GREEN);
            xLimitLine.setTextColor(Color.GREEN);
            xAxis.addLimitLine(xLimitLine);

            // 设置x轴的LimitLine
            LimitLine yLimitLine = new LimitLine(150f, "yLimit 测试");
            yLimitLine.setLineColor(Color.BLUE);
            yLimitLine.setTextColor(Color.BLUE);
            leftAxis.addLimitLine(yLimitLine);//添加限制线
            leftAxis.setDrawLimitLinesBehindData(false);//是否将限制先绘制在数据的下一层,默认false

            //6.创建描述信息
            Description description = new Description();
            description.setEnabled(false);//是否开启描述
            //如果禁用以下的设置全部不生效
//            description.setText("测试图表");
//            description.setTextColor(Color.RED);
//            description.setTextSize(20);
//            mLineChart.setDescription(description);//设置图表描述信息
//            mLineChart.setNoDataText("没有数据熬");//没有数据时显示的文字
//            mLineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
            mLineChart.setDescription(description);


//            mLineChart.setDrawGridBackground(true);//chart 绘图区后面的背景矩形将绘制
//            mLineChart.setGridBackgroundColor(Color.BLUE);//给背景矩形设置颜色
//            mLineChart.setDrawBorders(false);//禁止绘制图表边框的线

            //7.设置与图表交互
            mLineChart.setTouchEnabled(true); // 设置是否可以触摸,注意当这个属性设为false时，MarkerView不显示
            mLineChart.setDragEnabled(true);// 是否可以拖拽,false时MarkerView不能移动
            mLineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true,,false时MarkerView不能移动
            mLineChart.setScaleEnabled(false);// 是否可以缩放 x和y轴, 默认是true
            mLineChart.setScaleXEnabled(false); //是否可以缩放 仅x轴
            mLineChart.setScaleYEnabled(false); //是否可以缩放 仅y轴
            mLineChart.setPinchZoom(false);  //设置x轴和y轴能否同时缩放。默认是否
            mLineChart.setDoubleTapToZoomEnabled(false);//设置是否可以通过双击屏幕放大图表。默认是true
            mLineChart.setDragDecelerationEnabled(false);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
            mLineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

            //8.设置图例
            Legend l = mLineChart.getLegend();//创建图例
            l.setTextColor(Color.TRANSPARENT);//设置图例提示文字颜色,设置透明色表示隐藏
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置图例的位置
            l.setTextSize(10f);//设置文字大小
            l.setForm(Legend.LegendForm.NONE);//正方形，圆形或线 Legend.LegendForm.NONE表示不显示
            l.setFormSize(10f); // 设置Form的大小
            l.setWordWrapEnabled(true);//是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
            l.setFormLineWidth(10f);//设置Form的宽度

            //9.设置MarkView提示 点击交点的小提示窗
            //自定义的MarkerView对象
            mLineChart.setDrawMarkers(true);
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
            mv.setHovered(true);
            //一定要设置这个玩意，不然到点击到最边缘的时候不会自动调整布局
            mv.setChartView(mLineChart);
            mLineChart.setMarker(mv);

            //10.设置动画
//            animateX( int durationMillis) :水平轴动画 在指定时间内 从左到右
//            animateY( int durationMillis) :垂直轴动画 从下到上
//            animateXY( int xDuration, int yDuration) :两个轴动画，从左到右，从下到上
            mLineChart.animateXY(1000, 1000);

         /*   float vx = entries.get(entries.size() - 1).getX();
            float vy = entries.get(entries.size() - 1).getY();
            MPPointD pixels = mLineChart.getTransformer(
                    set1.getAxisDependency()).getPixelForValues(vx, vy);
            mLineChart.highlightValue(new Highlight(
                            vx,
                            vy,
                            (float) pixels.x,
                            (float) pixels.y,
                            0,
                            set1.getAxisDependency()),
                    true);*/

            //11.刷新图表
            mLineChart.invalidate();


        }
    }


}
