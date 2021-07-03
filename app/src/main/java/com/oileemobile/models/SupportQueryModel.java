package com.oileemobile.models;

import java.io.Serializable;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-10 02:52
 **/
public class SupportQueryModel implements Serializable {
    private int id;
    private String body, file1, file2, file3, response, status, query_date;

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getFile1() {
        return file1;
    }

    public String getFile2() {
        return file2;
    }

    public String getFile3() {
        return file3;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public String getQuery_date() {
        return query_date;
    }
}
