package com.example.davinci.util;

import com.example.davinci.SelectionSpec;

/**
 * 常量类
 * Created By Mr.Bean
 */
public class Constants {
    public static final int CPU_CORE_NUMBER = CPUCoreNumber.getCPUCoreNum();
    public static final int EXECUTE_TASK = 1;
    public static final int SCAN_FINISH = 0;
    public static final int MAX_SELECTION_COUNT = SelectionSpec.getInstance().maxSelectable;
}
