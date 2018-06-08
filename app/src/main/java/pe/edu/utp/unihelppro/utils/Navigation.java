package pe.edu.utp.unihelppro.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Navigation {
    private static Navigation instance;

    private synchronized static void createInstance() {
        if (instance == null) {
            instance = new Navigation();
        }
    }

    public static Navigation getInstance() {
        if (instance == null) createInstance();
        return instance;
    }

    public void startActivity(Context context, Bundle bundle, String activity, boolean destroy){
        try {
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClassName(context,activity);
            context.startActivity(intent);
            if(destroy) ((Activity)(context)).finish();
        } catch ( ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startActivityForResult(Context context, Bundle bundle, String activity, int REQUEST_CODE){
        try {
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClassName(context,activity);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
        } catch ( ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startActivityAndReset(Context context,Bundle bundle, String activity, boolean destroy){
        try {
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClassName(context,activity);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Activity mActivity= (Activity)(context);
            if(destroy) mActivity.finish();

        } catch ( ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
