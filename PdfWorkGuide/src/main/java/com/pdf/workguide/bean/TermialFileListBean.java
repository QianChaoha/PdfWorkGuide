package com.pdf.workguide.bean;

import com.pdf.workguide.base.BaseBean;

import java.util.List;

/**
 * Description:
 * Data: 2018/11/11
 *
 * @author: cqian
 */
public class TermialFileListBean extends BaseBean {

    public List<DataBean> data;

    public class DataBean {
        /**
         * PositionFileId : 7
         * FileIssuedPositionUrl : \192.168.1.222\前端切图.docx
         * FileIssuedPositionRealUrl : D:\普中公司项目\宁波鼎安电器sop\PZProject.Web\Uploads\SopFile\192.168.1.222\前端切图.docx
         * IsIssued : true
         * IsReceive : false
         * ClientIsDownload : true
         * IsDeleted : false
         */

        public int PositionFileId;
        public String FileIssuedPositionUrl;
        public String FileIssuedPositionRealUrl;
        public boolean IsIssued;
        public boolean IsReceive;
        public boolean ClientIsDownload;
        public boolean IsDeleted;
    }
}
