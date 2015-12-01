package com.github.airst.database;

import com.github.airst.database.result.DBQueryResult;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: MySqlExecutor
 * User: qige
 * Date: 15/12/1
 * Time: 下午7:34
 */
public class MySqlExecutor extends BaseExecutor {

    private Map<String, DataSource> dataSources;

    public MySqlExecutor(Map<String, DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public Object execute(String sql) {
        String realSql = selectSQL(sql);
        if(realSql.startsWith("select") || realSql.startsWith("SELECT")) {
            return executeQuery(sql);
        } else if(realSql.startsWith("update") || realSql.startsWith("UPDATE")
                || realSql.startsWith("delete") || realSql.startsWith("DELETE")) {
            return executeUpdate(sql);
        }
        throw new RuntimeException("only select|update|delete");
    }

    public static String selectDataSource(String sql) {
        if(!sql.contains("--")) {
            throw new RuntimeException("not DataSource bean description,\n" +
                    "'--${datasource}--\n" +
                    "${sql}'");
        }
        String[] split = sql.split("--");
        if(split.length != 2) {
            throw new RuntimeException("SQL format error,\r\n'--${datasource}--\r\n${sql}'");
        }
        return split[0];
    }

    public static String selectSQL(String sql) {
        if(!sql.contains("--")) {
            throw new RuntimeException("not DataSource bean description,\n" +
                    "'--${datasource}--\n" +
                    "${sql}'");
        }
        String[] split = sql.split("--");
        if(split.length != 2) {
            throw new RuntimeException("SQL format error,\r\n'--${datasource}--\r\n${sql}'");
        }
        return split[1];
    }

    public DBQueryResult executeQuery(String sql) {
        try {
            return executeQuery(dataSources.get(selectDataSource(sql)), selectSQL(sql));
        } catch (Exception e) {
            throw new RuntimeException("sql执行错误：" + sql + ",", e);
        }
    }

    @Override
    public int executeUpdate(String sql) {
        return executeUpdate(dataSources.get(selectDataSource(sql)), selectSQL(sql));
    }

    private int executeUpdate(DataSource dataSource, String sql) {
        try {
            PreparedStatement ps = null;
            int rs;
            Connection conn = null;
            try {

                conn = DataSourceUtils.doGetConnection(dataSource);

                ps = conn.prepareStatement(sql);
                rs = ps.executeUpdate();
                ps.close();
                DataSourceUtils.doReleaseConnection(conn, dataSource);
            } catch (Exception e) {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    DataSourceUtils.doReleaseConnection(conn, dataSource);
                throw new Exception(e.getMessage());
            }

            return rs;
        } catch (Exception e) {
            throw new RuntimeException("sql执行错误：" + sql + ",", e);
        }
    }


    private DBQueryResult executeQuery(DataSource dataSource, String sql) throws Exception {

        DBQueryResult result = new DBQueryResult();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {

            conn = DataSourceUtils.doGetConnection(dataSource);

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();

            int columns = md.getColumnCount();
            //显示列,表格的表头
            for (int i = 1; i <= columns; i++) {
                result.getFields().add(md.getColumnLabel(i));
            }

            Map<String, String> typeMap = new HashMap<String, String>();
            //显示表格内容
            while (rs.next()) {
                ArrayList<Object> lineData = new ArrayList<Object>();
                for (int i = 1; i <= columns; i++) {
                    String key = result.getFields().get(i - 1);
                    Object object = super.filterValue(rs.getObject(i));
                    if (object != null && typeMap.get(key) == null)
                        typeMap.put(key, object.getClass().getName());
                    lineData.add(object);
                }
                result.getDatas().add(lineData);
            }
            result.setTypeMap(typeMap);
            rs.close();
            ps.close();
            DataSourceUtils.doReleaseConnection(conn, dataSource);
        } catch (Exception e) {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            if (conn != null)
                DataSourceUtils.doReleaseConnection(conn, dataSource);
            throw new Exception(e.getMessage());
        }

        return result;
    }

}
