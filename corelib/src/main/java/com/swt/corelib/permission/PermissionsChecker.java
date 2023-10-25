package com.swt.corelib.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: PermissionsChecker <br>
 * Description: <br>
 * Copyright (c) Hienao版权所有 2017 <br>
 * Created DateTime: 2017/3/5 0005 11:30
 * Created by Wentao.Shi.
 */
public class PermissionsChecker {
    private final Context mContext;

    private List<String> permissionList;
    public PermissionsChecker(Context context) {
        permissionList = new ArrayList<>();
        mContext = context.getApplicationContext();
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }
    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public String[] getNotGrantedPermissions(String... permissions){
        permissionList.clear();
        for (String permission : permissions) {
            if (grantedPermission(permission)) {
                continue;
            }
            permissionList.add(permission);
        }
        return permissionList.toArray(new String[permissionList.size()]);
    }

    // 判断该权限是否授权
    private boolean grantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

}
