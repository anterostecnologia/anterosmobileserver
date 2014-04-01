/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package br.com.anteros.mobileserver.util;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class FieldTypes {

    public static final LinkedHashMap<String, String> types = new LinkedHashMap<String, String>();
    
    public static final String UNKNOW = "-- UNKNOW --";
    public static final String INTEGER = "INTEGER";
    public static final String FLOAT = "FLOAT";
    public static final String NUMERIC = "NUMERIC";
    public static final String VARCHAR = "VARCHAR";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String BLOB = "BLOB";
    public static final String CLOB = "CLOB";
    public static final String SUBSTITUITION = "SUBSTITUITION";

    public static final LinkedHashMap<String,String> getFieldTypes() {
        if (types.isEmpty()) {
            types.put("0", UNKNOW);
            types.put("4", INTEGER);
            types.put("6", FLOAT);
            types.put("2", NUMERIC);
            types.put("12", VARCHAR);
            types.put("91", DATE);
            types.put("92", TIME);
            types.put("93", TIMESTAMP);
            types.put("2004", BLOB);
            types.put("2005", CLOB);
            types.put("999999", SUBSTITUITION);
        }
        return types;
    }
    
    public static final List<Long> getFieldTypesValues(){
    	List result = new ArrayList(); 
    	result.add(0);
    	result.add(4);
    	result.add(6);
    	result.add(2);
    	result.add(12);
    	result.add(91);
    	result.add(92);
    	result.add(93);
    	result.add(2004);
    	result.add(2005);
    	return result;
    }
    
    
    public static final String getFieldNameByValue(String value){
    	Iterator<String> it = getFieldTypes().keySet().iterator();
    	while (it.hasNext()){
    		String key = it.next();
    		if (key.equals(value)){
    			return (String) getFieldTypes().get(key);
    		}
    	}
    	return  null;
    }
    
    public static final String getFieldValueByName(String name){
    	Iterator<String> it = getFieldTypes().keySet().iterator();
    	while (it.hasNext()){
    		String key = it.next();
    		String value = (String) getFieldTypes().get(key); 
    		if (value.equals(name)){
    			return key;
    		}
    	}
    	return  null;
    }
    
    public static final String convertJdbcType(int dataType){
    	String result = VARCHAR;
    	switch (dataType) {
		case Types.ARRAY:
			result = VARCHAR;
			break;
		case Types.BIGINT:
			result = INTEGER;
			break;
		case Types.BINARY:
			result = VARCHAR;
			break;
		case Types.BIT:
			result = INTEGER;
			break;
		case Types.BLOB:
			result = BLOB;
			break;
		case Types.BOOLEAN:
			result = INTEGER;
			break;
		case Types.CHAR:
			result = VARCHAR;
			break;
		case Types.CLOB:
			result = CLOB;
			break;
		case Types.DATALINK:
			result = VARCHAR;
			break;
		case Types.DATE:
			result = DATE;
			break;
		case Types.DECIMAL:
			result = NUMERIC;
			break;
		case Types.DISTINCT:
			result = VARCHAR;
			break;
		case Types.DOUBLE:
			result = NUMERIC;
			break;
		case Types.FLOAT:
			result = NUMERIC;
			break;
		case Types.INTEGER:
			result = INTEGER;
			break;
		case Types.JAVA_OBJECT:
			result = VARCHAR;
			break;
		case Types.LONGNVARCHAR:
			result = VARCHAR;
			break;
		case Types.LONGVARBINARY:
			result = BLOB;
			break;
		case Types.LONGVARCHAR:
			result = VARCHAR;
			break;
		case Types.NCHAR:
			result = VARCHAR;
			break;
		case Types.NCLOB:
			result = CLOB;
			break;
		case Types.NULL:
			result = VARCHAR;
			break;
		case Types.NUMERIC:
			result = NUMERIC;
			break;
		case Types.NVARCHAR:
			result = VARCHAR;
			break;
		case Types.OTHER:
			result = VARCHAR;
			break;
		case Types.REAL:
			result = NUMERIC;
			break;
		case Types.ROWID:
			result = VARCHAR;
			break;
		case Types.SMALLINT:
			result = INTEGER;
			break;
		case Types.SQLXML:
			result = VARCHAR;
			break;
		case Types.STRUCT:
			result = VARCHAR;
			break;
		case Types.TIME:
			result = TIME;
			break;
		case Types.TIMESTAMP:
			result = TIMESTAMP;
			break;
		case Types.TINYINT:
			result = INTEGER;
			break;
		case Types.VARBINARY:
			result = BLOB;
			break;
		case Types.VARCHAR:
			result = VARCHAR;
			break;
		default:
			break;
		}
    	
    	return result;
    }
}
