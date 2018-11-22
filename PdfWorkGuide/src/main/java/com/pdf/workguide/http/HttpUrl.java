package com.pdf.workguide.http;

/**
 * Description:
 * Data: 2018/7/2
 *
 * @author: cqian
 */

public class HttpUrl {
    public static final String SERVER_URL = "http://192.168.1.110:8018/";
    public static final String FILE_SERVER_URL = "http://192.168.1.110:8018/Uploads/SopFile";
//    public static final String SERVER_URL = "http://101.200.50.2:8035/";
//    public static final String FILE_SERVER_URL = "http://101.200.50.2:8035/Uploads/SopFile";

    public static final String LOGIN = "Api/Login";
    public static final String GET_POSITION_INFO = "Api/GetPositionInfo";
    //工位不良数
    public static final String GET_POSITION_BAD_NUMBER = "Api/GetPositionBadNumber";
    //工位不良类别信息列表
    public static final String GET_POSITION_BAD_INFO = "Api/GetPositionBadInfo";
    //添加工位不良
    public static final String GET_POSITION_BAD = "Api/AddPositionBad";
    //终端操作Sop文件列表
    public static final String GET_TERMINAL_FILE_LIST = "Api/GetTerminalPositionFileList";
    //终端操作Sop文件列表更新状态
    public static final String TERMINAL_FILE_EDIT = "Api/TerminalPositionFileEdit";
    //注销登陆
    public static final String LOG_OUT = "Api/LogOut";
}
