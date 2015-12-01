package com.github.airst.database.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: DBQueryResult
 * User: qige
 * Date: 15/12/1
 * Time: 下午8:26
 */
public class DBQueryResult implements Serializable {

    private static final long serialVersionUID = -7337192163527619732L;

    private List<String> fields = new ArrayList<String>();

    private List<List<Object>> datas = new ArrayList<List<Object>>();

    private Map<String, String> typeMap = new HashMap<String, String>();

    public List<List<Object>> getDatas() {
        return datas;
    }

    public void setDatas(List<List<Object>> datas) {
        this.datas = datas;
    }

    public Map<String, String> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, String> typeMap) {
        this.typeMap = typeMap;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

}
