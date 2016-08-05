package org.edx.mobile.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.edx.mobile.logger.Logger;
import org.edx.mobile.module.db.DbStructure;

import java.io.File;
import java.util.Collections;

public abstract class SecurityUtil {
    private static final Logger logger = new Logger(SecurityUtil.class);

    public static void clearUserData(@NonNull Context context) {
        // Clear the App's directory
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            File appDir = new File(packageInfo.applicationInfo.dataDir);
            if (appDir.exists() && appDir.isDirectory()) {
                File[] children = appDir.listFiles();
                for (int size = children.length, i = 0; i < size; i++) {
                    FileUtil.deleteRecursive(children[i],
                            Collections.singletonList(DbStructure.NAME));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Should never happen as we've given our app's package name to getPackageInfo function.
            logger.error(e);
        }

        // Now clear the App's external storage directory
        File externalDir = new File(context.getExternalFilesDir(null).getParent());
        if (externalDir.exists() && externalDir.isDirectory()) {
            File[] children = externalDir.listFiles();
            for (int size = children.length, i = 0; i < size; i++) {
                FileUtil.deleteRecursive(children[i],
                        Collections.singletonList(AppConstants.Folders.VIDEOS));
            }
        }

    }
}
