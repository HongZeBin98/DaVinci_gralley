package com.example.davinci.util;

import com.example.davinci.SelectionSpec;

/**
 * 常量类
 * Created By Mr.Bean
 */
public class Constants {
    static final int CPU_CORE_NUMBER = CPUCoreNumber.getCPUCoreNum();
    static final int EXECUTE_TASK = 36541;
    public static final int SCAN_FINISH = 1231354;
    public static final int MAX_SELECTION_COUNT = SelectionSpec.getInstance().maxSelectable;

    // 缩放模式
    public static final int MODE_SCALE = 10002;
    // 平移模式
    public static final int MODE_FLING = 10000;
    // 静止模式
    public static final int MODE_FREE = 10001;
}
