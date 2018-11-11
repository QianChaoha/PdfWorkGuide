package com.pdf.workguide.bean;

import com.pdf.workguide.base.BaseBean;

import java.util.List;

/**
 * Description:
 * Data: 2018/11/11
 *
 * @author: cqian
 */
public class PositionBadInfoBean extends BaseBean {

    public List<DataBean> data;

    public class DataBean {
        /**
         * Id : 1
         * ProcessErrorCode : Bad
         * ProcessErrorName : 不良问题1
         * Sort : 1
         */

        public int Id;
        public String ProcessErrorCode;
        public String ProcessErrorName;
        public int Sort;
    }
}
