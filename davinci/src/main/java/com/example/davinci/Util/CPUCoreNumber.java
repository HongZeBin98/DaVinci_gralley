package com.example.davinci.Util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * 获取CPU中的核心数
 * Created By Mr.Bean
 */
public class CPUCoreNumber {
    /**
     *  获取CPU中的核心数
     * @return CPU核心数
     */
    public static int getCPUCoreNum(){
        File dir = new File("/sys/devices/system/cpu/");
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return Pattern.matches("cpu[0-9]", file.getName());
            }
        });
        return files.length;
    }
}
