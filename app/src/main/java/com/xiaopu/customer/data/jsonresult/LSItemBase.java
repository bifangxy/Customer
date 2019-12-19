package com.xiaopu.customer.data.jsonresult;

import java.io.Serializable;

/**
 * 创建时间:2015年07月24日 上午11:40</br>
 * 功能描述:</br>
 * 注意事项:</br>
 * ------------1.本类由lucifd开发,阅读、修改时请勿随意修改代码排版格式后提交到版本控制器。本人有十分严重的代码洁癖!</br>
 * ------------2.本类仅供阅读使用,如需修改此类可将此类继承后对子类进行操作.</br>
 * ------------3.如需在本类内部进行修改,请先联系lucifd,若未经同意修改此类后造成损失本人概不负责</br>
 *
 * @author lucifd@sina.cn
 */
public class LSItemBase implements Serializable {
    /**
     * 类标签
     */
    public static final String LOG_TAG = "LSItemBase";

    public String date;
    public String info;

    // 尿检检测结果对象
    public static class NJLSItem extends LSItemBase implements Serializable {

        public static final String Status_Low = "阴性";
        public static final String Status_Ok = "阳性";
        public static final String Status_High = "其他";

        public String leuValue;
        public String leuStandard;
        public String leuStatus;

        public String nitValue;
        public String nitStandard;
        public String nitStatus;

        public String ubgValue;
        public String ubgStandard;
        public String ubgStatus;

        public String proValue;
        public String proStandard;
        public String proStatus;

        public String phValue;
        public String phStandard;
        public String phStatus;

        public String bldValue;
        public String bldStandard;
        public String bldStatus;

        public String sgValue;
        public String sgStandard;
        public String sgStatus;

        public String ketValue;
        public String ketStandard;
        public String ketStatus;

        public String bilValue;
        public String bilStandard;
        public String bilStatus;

        public String gluValue;
        public String gluStandard;
        public String gluStatus;

        public String vcValue;
        public String vcStandard;
        public String vcStatus;

        public String sourceData;

        public NJLSItem() {
        }

    }


    // 孕检检测结果对象
    public static class YJLSItem extends LSItemBase {

        //孕检检测颜色
        public String pregnancyColor1;
        public String pregnancyColor2;

        //孕检检测结果:0 未怀孕  1已怀孕 2，无效
        public int pregnancyResult;

        //源数据
        public String sourceData;
    }


    //排卵检测
    public static class PLItem extends LSItemBase {
        //排卵检测颜色
        public String ovulationColor1;
        public String ovulationColor2;
        //排卵检测结果: 0 未排卵  1  已排卵,2 无效
        public int ovulationResult;

        //源数据
        public String sourceData;
    }

    //体重检测结果对象
    public static class TZItem extends LSItemBase {
        public String tzValue;
        public String tzStandard;
        public String tzStatus;
    }

    // 人体成分检测结果对象
    public static class RTCFLSItem extends LSItemBase {
        public static final String Status_Low = "阴性";
        public static final String Status_Ok = "阳性";
        public static final String Status_High = "其他";

        public String tzValue; //体重
        public String tzStandard;
        public String tzStatus;

        /*
        脂肪
         */
        public String zfValue;
        public String zfStandard;
        public String zfStatus;

        /*
        水分
         */
        public String sfValue;
        public String sfStandard;
        public String sfStatus;

        /*
        蛋白质
         */
        public String dbzValue;
        public String dbzStandard;
        public String dbzStatus;

        /*
        骨量
         */
        public String glValue;
        public String glStandard;
        public String glStatus;

        /*
        BMI
         */
        public String bmiValue;
        public String bmiStandard;
        public String bmiStatus;

        /*
        肌肉
         */
        public String jrValue;
        public String jrStandard;
        public String jrStatus;

        /*
        内脏脂肪
         */
        public String nzzfValue;
        public String nzzfStandard;
        public String nzzfStatus;

        /*
        BMR
         */
        public String bmrValue;
        public String bmrStandard;
        public String bmrStatus;

        /*
        阻抗
         */
        public String zkValue;
        public String sourceData;

        public String gluValue;
        public String gluStandard;
        public String gluStatus;

        public String vcValue;
        public String vcStandard;
        public String vcStatus;
    }

    // 心电检测结果对象
    public static class XDLSItem extends LSItemBase {
        public byte[] cardiotach;
        public double averageCardiotach;
        public String result;
    }
}

