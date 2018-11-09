package com.pdf.workguide.util;

import android.Manifest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanxw
 */

public class Constant {

    /**
     * 所有需要申请的权限。此方法RN也会调用,新的权限只能向后添加
     * 0.相机
     * 1.存储
     */
    public static final String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,};

}
