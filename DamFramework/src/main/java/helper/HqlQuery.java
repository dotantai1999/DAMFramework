package helper;

import Repository.DBConnectionImpl;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HqlQuery {
    private String query;
    private String targetQuery;
    private Map<Integer, Object> parameter = new HashMap<>();

    public HqlQuery() {
    }

    public HqlQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTargetQuery() {
        return targetQuery;
    }

    public void setTargetQuery(String query) {
        this.targetQuery = query;
    }

    public void createSql(){
        String className = Helper.getClassNameFromQuery(this.query);
        Class zClass = Helper.getClassObjectWithClassName(className);
        String desQuery = replaceClassName(zClass, className);
        HashMap<String, String> mapAttrCol = Helper.getMapAttributeColumn(zClass);
        for(String attrName : mapAttrCol.keySet()){
            String colName = mapAttrCol.get(attrName);
            String regex = "\\b" + attrName;
            desQuery = desQuery.replaceAll(regex, colName);
        }

        this.targetQuery = desQuery;
    }

    private String replaceClassName(Class zClass, String className){
        String tableName = Helper.getTableName(zClass);
        String regex = "\\b" + className;
        String queryClone = this.query;
        return queryClone.replaceAll(regex, tableName);
    }

    public void setParamter(int number, Object value){
        this.parameter.put(number,value);
    }

    public List<Map<String, Object>> excuteQuery() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        List<Map<String, Object>> result = new ArrayList<>();

        conn = DBConnectionImpl.getConnection();
        try {
            statement = conn.prepareStatement(this.targetQuery);
            for(int index : parameter.keySet()) {
                statement.setObject(index, parameter.get(index));
            }
            rs = statement.executeQuery();
            rsmd = (ResultSetMetaData) rs.getMetaData();

            List<String> listColumnName = new ArrayList<>();
            for(int i = 1; i<= rsmd.getColumnCount(); i++) {
                listColumnName.add(rsmd.getColumnName(i));
            }

            while(rs.next()) {
                Map<String, Object> temp = null;
                for(String columnName : listColumnName) {
                    temp = new HashMap<>();
                    temp.put(columnName,rs.getObject(columnName));
                }
                result.add(temp);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

}
