package zj.zfenlly.arc;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/10/10.
 */

public class EvilInstrumentation extends Instrumentation {

    private static final String TAG = "EvilInstrumentation";

    // ActivityThread中原始的对象, 保存起来
    Instrumentation mBase;

    public EvilInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public void execStart(String a) {
        Log.e(TAG, "execStart            ???" + a);
    }

    public void callActivityOnCreate(Activity activity, Bundle icicle) {

        // Hook之前, XXX到此一游!
        Log.e(TAG, "\n执行了callActivityOnCreate, 参数如下: \n" + "Activity = [" + activity.toString() + "], " +
                "\nBundle  = [" + icicle.toString() + "]");

        // 开始调用原始的方法, 调不调用随你,但是不调用的话, 所有的startActivity都失效了.
        // 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法

        try {
            Method callActivityOnCreate = Instrumentation.class.getDeclaredMethod(
                    "callActivityOnCreate",
                    Activity.class, Bundle.class);
            callActivityOnCreate.setAccessible(true);
            callActivityOnCreate.invoke(mBase, activity, icicle);
        } catch (Exception e) {
            // 某该死的rom修改了  需要手动适配
            throw new RuntimeException("do not support!!! pls adapt it");
        }
    }
}