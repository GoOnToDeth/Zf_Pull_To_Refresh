package com.wzf.ptrdemos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ===============================
 * 描    述：
 * 作    者：wzf
 * 创建日期：2017/9/12 18:02
 * ===============================
 */
public class DataSource {

    private int stepLength = 20;

    private static DataSource sDataSource;

    public static DataSource getInstance() {
        if (sDataSource == null)
            sDataSource = new DataSource();
        return sDataSource;
    }

    public List<String> getSource() {
        List<String> mStringList = new ArrayList<>();
        for (int i = 0; i < stepLength; i++) {
            mStringList.add(String.valueOf(getRandom()));
        }
        return mStringList;
    }

    int max = 10000;
    int min = 1;
    Random random = new Random();

    private int getRandom() {
        return random.nextInt(max) % (max - min + 1) + min;
    }
}
