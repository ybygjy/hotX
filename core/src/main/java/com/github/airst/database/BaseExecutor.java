package com.github.airst.database;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Description: BaseExecutor
 * User: qige
 * Date: 15/12/1
 * Time: 下午8:24
 */
public abstract class BaseExecutor implements DBExecutor {

    protected Object filterValue(Object obj){
        if(obj==null)
            return null;
        if(obj instanceof BigInteger){
            return Long.parseLong(obj+"");
        }else if(obj instanceof BigDecimal){
            BigDecimal bigDecimal=  (BigDecimal) obj;
            return bigDecimal.doubleValue();
        }
        return obj;
    }

}
