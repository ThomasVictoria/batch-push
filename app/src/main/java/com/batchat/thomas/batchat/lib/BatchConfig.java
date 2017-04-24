package com.batchat.thomas.batchat.lib;

import com.batch.android.Batch;
import com.batch.android.Config;

/**
 * Created by thomas on 03/04/2017.
 */

public class BatchConfig {

    public static void start(){
        Batch.setConfig(new Config(""));
        Batch.Push.setGCMSenderId("");
    }
}
