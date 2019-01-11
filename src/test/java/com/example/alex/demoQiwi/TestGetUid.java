package com.example.alex.demoQiwi;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class TestGetUid {

    @Test

    public void testGetUidFromUrl() {
        String url = "https://oplata.qiwi.com/form/?invoice_uid=d875277b-6f0f-445d-8a83-f62c7c07be77";
        String resultUid = StringUtils.substringAfter(url, "invoice_uid=");
        System.out.println(resultUid);
    }
}
