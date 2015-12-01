package com.github.airst.database;

import com.github.airst.database.result.DBQueryResult;

/**
 * Description: DBExecutor
 * User: qige
 * Date: 15/12/1
 * Time: 下午8:24
 */
public interface DBExecutor {

    DBQueryResult executeQuery(String sql);

    int executeUpdate(String sql);

}
