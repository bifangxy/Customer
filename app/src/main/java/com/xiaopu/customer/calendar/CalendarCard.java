package com.xiaopu.customer.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.DetectionMenstruation;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 自定义日历卡
 *
 * @author wuwenjie
 */
public class CalendarCard extends View {

    private static final String LOG_TAG = CalendarCard.class.getSimpleName();

    private Context mContext;

    private static final int TOTAL_COL = 7; // 7列

    private static final int TOTAL_ROW = 6; // 6行

    private Paint mRectPaint;//绘制方形画笔

    private Paint mRectPaintFull;//绘制方形画笔 填充类型

    private Paint mCellPaint;//绘制格子的间隙

    private Paint mTextPaint;//绘制文字画笔

    //    private Paint mCirclePaint; // 绘制圆形的画笔
    private int mViewWidth; //视图的宽度

    private int mViewHeight; // 视图的高度

    private int mCellSpace; // 单元格间距

    private float mCellWidth;//单元的宽

    private float mCellHeight;//单元的高

    private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行

    private static CustomDate mCustomDate; // 自定义的日期，包括year,month,day

    private OnCellClickListener mCellClickListener; // 单元格点击回调事件

    private int touchSlop; //

    private boolean callBackCellSpace;

    private Cell mClickCell;

    private float mDownX;

    private float mDownY;

    /**
     * 经期检测相关
     */
    private int menstruationTimeLenth;  //平均月经周期（3-8）

    private int menstruationCycle;  //平均行经周期

    private String lastMenstruationTime;  //上次月经时间

    private List<Integer> mIntList;//检测日期

    private List<Integer> list_menDay;//经期

    private List<Integer> list_Pailuanqi;//排卵期

    private DetectionMenstruation detectionMenstruation;

    /**
     * 单元格点击的回调接口
     *
     * @author wuwenjie
     */
    public interface OnCellClickListener {
        void clickDate(CustomDate date); // 回调点击的日期

        void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
    }

    public CalendarCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCard(Context context) {
        super(context);
        init(context);
    }

    public CalendarCard(Context context, OnCellClickListener listener) {
        super(context);
        this.mContext = context;
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {
//        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mCirclePaint.setStyle(Paint.Style.FILL);
//        mCirclePaint.setColor(Color.parseColor("#F24949")); // 红色圆形

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.parseColor("#00c9e1"));

        mCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCellPaint.setColor(Color.parseColor("#dddddd"));//ffffff
        mCellPaint.setStrokeWidth(2);

        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(Color.parseColor("#00c9e1"));//00c9e1
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(2);

        mRectPaintFull = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaintFull.setColor(Color.parseColor("#ff80c7"));
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initDate();
    }

    private void initDate() {
        mCustomDate = new CustomDate();
        list_menDay = new ArrayList<>();
        list_Pailuanqi = new ArrayList<>();
        fillDate();
    }

    public void returnToday() {
        initDate();
    }

    private void fillDate() {
        int monthDay = DateUtil.getCurrentMonthDay();
        int lastMonthDays = DateUtil.getMonthDays(mCustomDate.year, mCustomDate.month - 1);
        int currentMonthDays = DateUtil.getMonthDays(mCustomDate.year, mCustomDate.month);
        int firstDayWeek = DateUtil.getWeekDayFromDate(mCustomDate.year, mCustomDate.month);

        boolean isCurrentMonth = false;

        if (ApplicationXpClient.userInfoResult.getSex() == 0) {
            if (ApplicationXpClient.detectionMenstruation != null) {
                detectionMenstruation = ApplicationXpClient.detectionMenstruation;
                getOvulation();
            } else {
                HttpUtils.getInstantce().postNoHead(HttpConstant.Url_getMenstruationData, new HttpCallBack<DetectionMenstruation>() {
                    @Override
                    public void onSuccess(HttpResult result) {
                        detectionMenstruation = (DetectionMenstruation) result.getData();
                        ApplicationXpClient.detectionMenstruation = detectionMenstruation;
                        fillDate();
                    }

                    @Override
                    public void onFail(String msg) {
                        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("提示")
                                .setContentText("获取经期信息失败，请稍后重试")
                                .setConfirmText("确定")
                                .show();
                    }
                });

               /* HttpUtils.getInstantce().getMenstruationData(new HttpConstant.SampleJsonResultListener<Feedback<DetectionMenstruation>>() {
                    @Override
                    public void onSuccess(Feedback<DetectionMenstruation> jsonData) {
                        ApplicationXpClient.detectionMenstruation = jsonData.getData();
                        detectionMenstruation = jsonData.getData();
                        fillDate();
                    }

                    @Override
                    public void onFailure(Feedback<DetectionMenstruation> jsonData) {
                        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("提示")
                                .setContentText("获取经期信息失败，请稍后重试")
                                .setConfirmText("确定")
                                .show();
                    }
                });*/
            }
        }


        if (DateUtil.isCurrentMonth(mCustomDate))

        {
            isCurrentMonth = true;
        }

        int day = 0;

        for (
                int j = 0;
                j < TOTAL_ROW; j++)

        {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL;
                // 这个月的
                if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {
                    day++;
                    CustomDate date = CustomDate.modifiDayForObject(mCustomDate, day);

                    if (list_menDay.contains(day)) {
                        rows[j].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, j, 1);
                        if (isCurrentMonth && day == monthDay) {
                            rows[j].cells[i] = new Cell(date, State.TODAY, i, j, 1);
                        }
                        if (isCurrentMonth && day > monthDay) {
                            rows[j].cells[i] = new Cell(date, State.UNREACH_DAY, i, j, 1);
                        }

                    } else if (list_Pailuanqi.contains(day)) {
                        rows[j].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, j, 3);
                        if (isCurrentMonth && day == monthDay) {
                            rows[j].cells[i] = new Cell(date, State.TODAY, i, j, 3);
                        }
                        if (isCurrentMonth && day > monthDay) {
                            rows[j].cells[i] = new Cell(date, State.UNREACH_DAY, i, j, 3);
                        }
                    } else {
                        if (ApplicationXpClient.userInfoResult.getSex() == 1) {
                            rows[j].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, j, 0);
                            if (isCurrentMonth && day == monthDay) {
                                rows[j].cells[i] = new Cell(date, State.TODAY, i, j, 0);
                            }
                            if (isCurrentMonth && day > monthDay) {
                                rows[j].cells[i] = new Cell(date, State.UNREACH_DAY, i, j, 0);
                            }
                        } else {
                            rows[j].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, j, 2);
                            if (isCurrentMonth && day == monthDay) {
                                rows[j].cells[i] = new Cell(date, State.TODAY, i, j, 2);
                            }
                            if (isCurrentMonth && day > monthDay) {
                                rows[j].cells[i] = new Cell(date, State.UNREACH_DAY, i, j, 2);
                            }
                        }
                    }
                    // 过去一个月
                } else if (position < firstDayWeek) {
                    rows[j].cells[i] = new Cell(new CustomDate(
                            mCustomDate.year, mCustomDate.month - 1, lastMonthDays - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i, j, 0);
                    // 下个月
                } else if (position >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell(new CustomDate(
                            mCustomDate.year, mCustomDate.month + 1, position - firstDayWeek - currentMonthDays + 1),
                            State.NEXT_MONTH_DAY, i, j, 0);
                }
            }
        }
        mCellClickListener.changeDate(mCustomDate);
    }

    /**
     * 根据获取到的进行过检测的天数，对原本cell的状态进行修改
     */
    private void changeCell() {
        int monthDay = DateUtil.getCurrentMonthDay();
        int firstDayWeek = DateUtil.getWeekDayFromDate(mCustomDate.year, mCustomDate.month);
        for (int j = 0; j < mIntList.size(); j++) {
            int i = mIntList.get(j);
            int c = (firstDayWeek + i - 1) / 7;
            int d = (firstDayWeek + i - 1) % 7;
            if (i == monthDay) {
                changeCell(rows[c].cells[d], State.TODAY_AND_CHECK);
            } else {
                changeCell(rows[c].cells[d], State.CHECK);
            }
        }

    }

    private void changeCell(Cell cell, State state) {
        cell.state = state;
    }

    public void updateCheck(List<Integer> list) {
        mIntList = list;
        changeCell();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#ffffff"));  // f5f5f5
        for (int j = 1; j < 7; j++) {
            canvas.drawLine(mCellWidth * j, 0, mCellWidth * j, mViewHeight, mCellPaint);
        }
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (i > 0) {
                canvas.drawLine(0, mCellHeight * i, mViewWidth, mCellHeight * i, mCellPaint);
            }
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCellHeight = mViewHeight / TOTAL_ROW;
        mCellWidth = mViewWidth / TOTAL_COL;
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellWidth);
                    int row = (int) (mDownY / mCellHeight);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 计算点击的单元格
     *
     * @param col
     * @param row
     */
    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            mClickCell = new Cell(rows[row].cells[col].date, rows[row].cells[col].state, rows[row].cells[col].i, rows[row].cells[col].j, 0);
            CustomDate date = rows[row].cells[col].date;
            date.week = col;
            mCellClickListener.clickDate(date);
            // 刷新界面
            update();
        }
    }

    /**
     * 组元素
     *
     * @author wuwenjie
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }

    }

    /**
     * 单元格元素
     *
     * @author wuwenjie
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        public int j;
        public int type;

        public Cell(CustomDate date, State state, int i, int j, int type) {
            super();
            this.date = date;
            this.state = state;
            this.type = type;
            this.i = i;
            this.j = j;
        }

        public void drawSelf(Canvas canvas) {
            switch (state) {
                case CHECK:
                    drawCell(type, canvas, i, j);
                    Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.xing);
                    canvas.drawBitmap(rawBitmap, (float) (mCellWidth * (i + 0.4)),
                            ((j) * mCellHeight + mCellHeight * 2 / 3), mRectPaint);
                    break;
                case TODAY_AND_CHECK:
                    drawCell(type, canvas, i, j);
                    Bitmap rawBitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.xing);
                    canvas.drawBitmap(rawBitmap1, (float) (mCellWidth * (i + 0.4)),
                            ((j) * mCellHeight + mCellHeight * 2 / 3), mRectPaint);
                    canvas.drawRect(mCellWidth * i + 1, mCellHeight * j + 1, mCellWidth * (i + 1) - 1, mCellHeight * (j + 1) - 1, mRectPaint);
                    break;
                case TODAY: // 今天
                    drawCell(type, canvas, i, j);
                    canvas.drawRect(mCellWidth * i + 1, mCellHeight * j + 1, mCellWidth * (i + 1) - 1, mCellHeight * (j + 1) - 1, mRectPaint);

                    break;
                case CURRENT_MONTH_DAY: // 当前月日期

                    drawCell(type, canvas, i, j);

                    break;
                case PAST_MONTH_DAY:
                    // 过去一个月
                case NEXT_MONTH_DAY: // 下一个月
                    mTextPaint.setColor(Color.parseColor("#cccccc"));
                    break;
                case UNREACH_DAY: // 还未到的天
                    /*mTextPaint.setColor(Color.GRAY);*/
                    drawCell(type, canvas, i, j);
                    break;
                default:
                    break;
            }

            // 绘制文字
            String content = date.day + "";
            canvas.drawText(content, (i * mCellWidth) + (mCellWidth / 2), (j * mCellHeight) + (mCellHeight * 2 / 5), mTextPaint);
        }
    }

    /**
     * @author wuwenjie 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */
    enum State {
        TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY, TODAY_AND_CHECK, CHECK;
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        if (mCustomDate.month == 1) {
            mCustomDate.month = 12;
            mCustomDate.year -= 1;
        } else {
            mCustomDate.month -= 1;
        }
        update();
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mCustomDate.month == 12) {
            mCustomDate.month = 1;
            mCustomDate.year += 1;
        } else {
            mCustomDate.month += 1;
        }
        update();
    }

    public void update() {
        fillDate();
        invalidate();
    }

    public void checkPageReturn(int year, int month) {
        mCustomDate.year = year;
        mCustomDate.month = month;
        update();
    }

    public void changeYear(int num) {
        mCustomDate.year = num;
        update();
    }


    private void drawCell(int type, Canvas canvas, int i, int j) {
        if (type == 3) {
            mTextPaint.setColor(Color.parseColor("#9362d3"));
        } else if (type == 2) {
            mTextPaint.setColor(Color.parseColor("#8fc31e"));
        } else if (type == 1) {
            mTextPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRect(mCellWidth * i + 1, mCellHeight * j + 1, mCellWidth * (i + 1) - 1, mCellHeight * (j + 1) - 1, mRectPaintFull);
        } else if (type == 0) {
            mTextPaint.setColor(Color.parseColor("#5f5f5f"));
        }
    }

    private void getOvulation() {
        list_menDay.clear();

        list_Pailuanqi.clear();

        menstruationCycle = detectionMenstruation.getAverageMenstrual();

        menstruationTimeLenth = detectionMenstruation.getAverageMenstrualPeriod();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        lastMenstruationTime = sdf.format(detectionMenstruation.getMenstrualTime());

        int menstruationYear = Integer.parseInt(lastMenstruationTime.split("-")[0]);

        int menstruationMonth = Integer.parseInt(lastMenstruationTime.split("-")[1]);

        int menstruationDay = Integer.parseInt(lastMenstruationTime.split("-")[2]);

        Date LastMenstuationDay = new Date(menstruationYear, menstruationMonth - 1, menstruationDay, 0, 0, 0);

        int daysOfMonth = DateUtil.getMonthDays(mCustomDate.getYear(), mCustomDate.getMonth());

        for (int i = 1; i <= daysOfMonth; i++) {
            Date myDate = new Date(mCustomDate.getYear(), mCustomDate.getMonth() - 1, i, 0, 0, 0);
            double myTime = Math.floor((myDate.getTime() - LastMenstuationDay.getTime()) / (24 * 60 * 60 * 1000));
            double someDate = (myTime % menstruationCycle + menstruationCycle) % menstruationCycle;
            if (someDate >= 0 && someDate <= (menstruationTimeLenth - 1)) {
                list_menDay.add(i);
            } else if (someDate >= (menstruationCycle - 19) && someDate <= (menstruationCycle - 10)) {
                list_Pailuanqi.add(i);
            }

        }
    }

    public List<Integer> getList_Pailuanqi() {
        return list_Pailuanqi;
    }

    public void setList_Pailuanqi(List<Integer> list_Pailuanqi) {
        this.list_Pailuanqi = list_Pailuanqi;
    }

    public List<Integer> getList_menDay() {

        return list_menDay;
    }

    public void setList_menDay(List<Integer> list_menDay) {
        this.list_menDay = list_menDay;
    }
}
