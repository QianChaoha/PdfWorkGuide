package com.pdf.workguide.bean;

/**
 * Description:
 * Data: 2018/11/12
 *
 * @author: cqian
 */
public class DownloadFileBean {
    public String remotePath;
    public String fileName;

    public DownloadFileBean(String remotePath, String fileName) {
        this.remotePath = remotePath;
        this.fileName = fileName;
    }
}
