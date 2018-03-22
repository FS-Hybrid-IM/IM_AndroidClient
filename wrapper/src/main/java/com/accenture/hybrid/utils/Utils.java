package com.accenture.hybrid.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by bo.e.liu on 21/03/2018.
 */

public class Utils {

    public static Properties getNetConfigProperties() {

        Properties props = new Properties();
        try {
            InputStream in = Utils.class.getResourceAsStream("/assets/netConfig.properties");
            props.load(in);
        } catch (Exception e1) {

            e1.printStackTrace();
        }

        return props;
    }
}
