package com.pdf.workguide.bean;

import com.pdf.workguide.base.BaseBean;

import java.util.List;

/**
 * Description:
 * Data: 2018/11/9
 *
 * @author: cqian
 */
public class PositionInfoBean extends BaseBean {

    /**
     * data : {"errorCategoryList":[{"CodeCategory":"ProcessErrorCategory","Code":"Bad","CodeDescribe":"不良","Id":3,"AddedBy":1,"AddedDate":"2018-11-07T15:00:54.08","IsDeleted":false,"UpdatedBy":null,"UpdatedDate":null,"VersionNumber":1},{"CodeCategory":"ProcessErrorCategory","Code":"InternalInspection","CodeDescribe":"内检","Id":4,"AddedBy":1,"AddedDate":"2018-11-07T15:00:54.08","IsDeleted":false,"UpdatedBy":null,"UpdatedDate":null,"VersionNumber":1},{"CodeCategory":"ProcessErrorCategory","Code":"OutsideInspection","CodeDescribe":"外检","Id":5,"AddedBy":1,"AddedDate":"2018-11-07T15:00:54.09","IsDeleted":false,"UpdatedBy":null,"UpdatedDate":null,"VersionNumber":1}],"terminalInfo":{"PositionName":"产线1工位1","LoginName":"admin","ProductName":"产品名称1","ProductCode":"BM00001","ProcessName":"工序名称1","ProductPlanNumber":0,"ProductPlanDate":null,"FileName":"图标.rar","FileIssuedPositionUrl":"\\192.168.1.222\\图标.rar","FileIssuedPositionRealUrl":"D:\\鼎安\\Uploads\\SopFile\\192.168.1.222\\图标.rar","FileSwitchingTime":10,"FilePlaybackTime":10}}
     */

    public DataBean data;

    public class DataBean {
        /**
         * errorCategoryList : [{"CodeCategory":"ProcessErrorCategory","Code":"Bad","CodeDescribe":"不良","Id":3,"AddedBy":1,"AddedDate":"2018-11-07T15:00:54.08","IsDeleted":false,"UpdatedBy":null,"UpdatedDate":null,"VersionNumber":1},{"CodeCategory":"ProcessErrorCategory","Code":"InternalInspection","CodeDescribe":"内检","Id":4,"AddedBy":1,"AddedDate":"2018-11-07T15:00:54.08","IsDeleted":false,"UpdatedBy":null,"UpdatedDate":null,"VersionNumber":1},{"CodeCategory":"ProcessErrorCategory","Code":"OutsideInspection","CodeDescribe":"外检","Id":5,"AddedBy":1,"AddedDate":"2018-11-07T15:00:54.09","IsDeleted":false,"UpdatedBy":null,"UpdatedDate":null,"VersionNumber":1}]
         * terminalInfo : {"PositionName":"产线1工位1","LoginName":"admin","ProductName":"产品名称1","ProductCode":"BM00001","ProcessName":"工序名称1","ProductPlanNumber":0,"ProductPlanDate":null,"FileName":"图标.rar","FileIssuedPositionUrl":"\\192.168.1.222\\图标.rar","FileIssuedPositionRealUrl":"D:\\鼎安\\Uploads\\SopFile\\192.168.1.222\\图标.rar","FileSwitchingTime":10,"FilePlaybackTime":10}
         */

        public TerminalInfoBean terminalInfo;
        public List<ErrorCategoryListBean> errorCategoryList;

        public class TerminalInfoBean {
            /**
             * PositionName : 产线1工位1
             * LoginName : admin
             * ProductName : 产品名称1
             * ProductCode : BM00001
             * ProcessName : 工序名称1
             * ProductPlanNumber : 0
             * ProductPlanDate : null
             * FileName : 图标.rar
             * FileIssuedPositionUrl : \192.168.1.222\图标.rar
             * FileIssuedPositionRealUrl : D:\鼎安\Uploads\SopFile\192.168.1.222\图标.rar
             * FileSwitchingTime : 10
             * FilePlaybackTime : 10
             */

            public String PositionName;
            public String LoginName;
            public String ProductName;
            public String ProductCode;
            public String ProcessName;
            public int ProductPlanNumber;
            public String ProductPlanDate;
            public int ProductDetailId;
            public String FileName;
            public String FileIssuedPositionUrl;
            public String FileIssuedPositionRealUrl;
            public int FileSwitchingTime;
            public int FilePlaybackTime;
        }

        public class ErrorCategoryListBean {
            /**
             * CodeCategory : ProcessErrorCategory
             * Code : Bad
             * CodeDescribe : 不良
             * Id : 3
             * AddedBy : 1
             * AddedDate : 2018-11-07T15:00:54.08
             * IsDeleted : false
             * UpdatedBy : null
             * UpdatedDate : null
             * VersionNumber : 1
             */

            public String CodeCategory;
            public String Code;
            public String CodeDescribe;
            public int Id;
            public int AddedBy;
            public String AddedDate;
            public boolean IsDeleted;
            public Object UpdatedBy;
            public Object UpdatedDate;
            public int VersionNumber;
        }
    }
}
