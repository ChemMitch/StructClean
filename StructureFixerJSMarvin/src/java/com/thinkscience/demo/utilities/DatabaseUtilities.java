/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;

import com.thinkscience.demo.structurecleaner.objects.ifc.*;
import com.thinkscience.demo.structurecleaner.objects.impl.*;
import com.thinkscience.demo.structurecleaner.objects.impl.FieldData;
import com.thinkscience.demo.structurecleaner.objects.impl.FieldDataList;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mitch Miller
 */
public class DatabaseUtilities
{

	private String JDBC_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
	private static String SQL_COLUMN_DELIMITER = ", ";

	public Connection getOracleConnection(String server, String dbid, String port,
			String user, String pw)
	{
		Connection ret = null;
		try
		{
			Class.forName(JDBC_CLASS_NAME);
			String dbURL = String.format("jdbc:oracle:thin:@%s:%s:%s", server, port,
					dbid);
			ret = DriverManager.getConnection(dbURL, user, pw);

			SQLWarning warn = ret.getWarnings();
			while (warn != null)
			{
				System.out.println("SQLState: " + warn.getSQLState());
				System.out.println("Message: " + warn.getMessage());
				System.out.println("Vendor: " + warn.getErrorCode());
				System.out.println("");
				warn = warn.getNextWarning();
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return ret;
	}
/*
 * Extracts data from a ResultSet into an ArrayList
 */
	public ArrayList<IFieldDataList> getDataFromResultSet(ResultSet rs)
	{
		ArrayList<IFieldDataList> ret = new ArrayList<IFieldDataList>();
		boolean firstRecord = true;
		try
		{
			int recNum = 0;
			while (rs.next())
			{
				recNum++;
				FieldDataList rowData = new	FieldDataList();
				ArrayList<IFieldData> rowDataList = new ArrayList<IFieldData>();
				ResultSetMetaData fieldInfo = rs.getMetaData();
				fieldInfo.getColumnCount();
				for( int field=1; field<= rs.getMetaData().getColumnCount(); field++)
				{
//					if( firstRecord && field==1)
//					{
//						System.out.println("First ID: " + rs.getString(field));
//						firstRecord=false;
//					}
					Object columnData = null;
					if(rs.getMetaData().getColumnType(field) == java.sql.Types.CLOB)
					{
						columnData = rs.getString(field);
					}
					else
					{
						columnData = rs.getObject(field);
					}
					String dataAsString = "";
					try
					{
						dataAsString = columnData.toString();
						dataAsString = dataAsString.substring(0, 32);
					}
					catch(Exception ex)
					{
						//swallow the exception
					}

//					System.out.println("Looking at field " + field
//							+ " for record " + recNum + " value: " + dataAsString);

					IFieldData dataItem= new FieldData(
							rs.getMetaData().getColumnName(field), columnData);
					rowDataList.add(dataItem);
				}
				rowData.setData(rowDataList);
				ret.add(rowData);
			}
		} catch (SQLException sqe)
		{
			System.err.println( "error retrieving data: "  +sqe.getMessage());
		}
		return ret;
	}

	//Given a table name and a Map of table names to numbers, get the number,
	// prepend a 'T' to get returns that look like 'T1,' or 'T2,' etc.
	private static String getTableAliasForColumnName( HashMap<String, Integer> tableNameMap,
			String tableName, String prefix)
	{
		StringBuilder retBuilder = new StringBuilder();
		Integer tabIndex = tableNameMap.get(tableName);
		//make sure we have a valid value!
		if( tabIndex == null)
		{
			throw new IllegalArgumentException("Error!  No table named '"
					+ tableName + "' is present in the current query!");
		}
		retBuilder.append(" ");
		retBuilder.append(prefix);
		retBuilder.append(tabIndex);
		retBuilder.append(".");
		return retBuilder.toString();
	}

	public static IReturnedField parseQualifiedColumnName(String qualifiedColumnName)
	{
		IReturnedField ret = new ReturnedField();

		if (qualifiedColumnName.contains("[") && qualifiedColumnName.contains("]."))
		{
			//we have a table name as a reference
			int index1 = qualifiedColumnName.indexOf("[");
			int index2 = qualifiedColumnName.indexOf("]", index1);
			ret.setTableName(qualifiedColumnName.substring(index1 + 1, index2));

			//Table name may occur at start of qualified column or in the middle
			if (index1 > 0)
			{
				ret.setFieldName(qualifiedColumnName.substring(0, index1)
						+ qualifiedColumnName.substring(index2 + 2));
			} else
			{
				ret.setFieldName(qualifiedColumnName.substring(index2 + 2));
			}
		}
		else
		{
			//column expression does NOT contain a table reference
			ret.setFieldName(qualifiedColumnName);
		}
		return ret;
	}

	public static String createQueryFromComponents( ArrayList<String> columns,
			ArrayList<String> tables,
			ArrayList<IQueryClause> clauseItems,
			ArrayList<IQueryJoinItem> joinItems,
			ArrayList<String> presetQueryWhereClauses)
	{
		StringBuilder retBuilder = new StringBuilder();
		retBuilder.append(" FROM ");
		//keep track of which tables we have used
		HashMap<String, Integer> tablesToAliases = new HashMap<String, Integer>();
		for( int tableIndex=0;  tableIndex<tables.size(); tableIndex++)
		{
			if(tableIndex>0) retBuilder.append(",");
			retBuilder.append(" ");
			retBuilder.append(tables.get(tableIndex));
			//now, a space and a unique alias
			retBuilder.append(" T");
			retBuilder.append((tableIndex+1));
			tablesToAliases.put(tables.get(tableIndex), (tableIndex+1));
		}

		//now the list of columns, separately
		StringBuilder selectFields = new StringBuilder();
		selectFields.append("SELECT ");
		for( String column : columns)
		{
			IReturnedField fullColumnInfo = parseQualifiedColumnName(column);
			if( fullColumnInfo.getTableName() != null && fullColumnInfo.getTableName().length() >  0)
			{
				String tableAlias = getTableAliasForColumnName(tablesToAliases,
						fullColumnInfo.getTableName(), "t");
				String modifiedColumnName = column.replace("[" + fullColumnInfo.getTableName() +"].",
						tableAlias);
				selectFields.append(modifiedColumnName);
			}
			else
			{
				selectFields.append(fullColumnInfo.getFieldName());
			}
			
			selectFields.append(SQL_COLUMN_DELIMITER);
		}
		//Remove the final delimiter
		selectFields.delete(selectFields.lastIndexOf(SQL_COLUMN_DELIMITER),
				selectFields.length());
		selectFields.append(" ");
		retBuilder.append(" WHERE ");
		boolean atLeastOnePredicate = false;
		if( presetQueryWhereClauses.size() > 0 ) atLeastOnePredicate = true;

		for( int clauseIndex =0; clauseIndex < presetQueryWhereClauses.size();
		clauseIndex++)
		{
			if( clauseIndex > 0 ) retBuilder.append(" AND ");
			retBuilder.append(presetQueryWhereClauses.get(clauseIndex));
			atLeastOnePredicate = true;
		}
		//process predicates
		for( int clauseIndex = 0; clauseIndex < clauseItems.size(); clauseIndex++)
		{
			//only use conjunction for items beyond the first
			if( clauseIndex > 0 || atLeastOnePredicate)
			{
				retBuilder.append(" ");
				retBuilder.append(clauseItems.get(clauseIndex).getConjunction());
			}
			retBuilder.append( " ");
			//figure out the alias
			retBuilder.append(getTableAliasForColumnName(tablesToAliases, clauseItems.get(clauseIndex).getTableName(), "t"));
			retBuilder.append(clauseItems.get(clauseIndex).getFieldName());
			retBuilder.append(" ");
			retBuilder.append(clauseItems.get(clauseIndex).getFieldOperator());
			retBuilder.append(" ");
			retBuilder.append(clauseItems.get(clauseIndex).getFieldQueryValue());
			atLeastOnePredicate = true;
		}

		//now, the joins
		for( int joinIndex = 0; joinIndex < joinItems.size(); joinIndex++)
		{
			//append an AND
			if(joinIndex > 0 || atLeastOnePredicate)
			{
				retBuilder.append(" ");
				retBuilder.append(QueryClause.DEFAULT_CONJUNCTION);
				retBuilder.append(" ");
			}

			//append clauses of the form " t<n>.columnName = t<m>.columnName"
			IQueryJoinItem currJoinItem = joinItems.get(joinIndex);
			retBuilder.append(" t");
			retBuilder.append(tablesToAliases.get( currJoinItem.getLeftTableRerence()));
			retBuilder.append(".");
			retBuilder.append(currJoinItem.getLeftFieldName());
			retBuilder.append("=");
			retBuilder.append(" t");
			retBuilder.append(tablesToAliases.get( currJoinItem.getRightTableRerence()));
			retBuilder.append(".");
			retBuilder.append(currJoinItem.getRightFieldName());
		}

		return selectFields.toString() + retBuilder.toString();
	}
	
	public String saveMolfile(String molfile, String fileNameWithoutPath, Connection connection)
	{
		CallableStatement runProcedure;
		
		try
		{
			runProcedure = connection.prepareCall("{ call struct_clean_ops.write_File(?, ?)}");
			runProcedure.setString(1, fileNameWithoutPath);
			runProcedure.setString(2, molfile);
			boolean result = runProcedure.execute();//returns false even when this has worked so we ignore the result
			
			runProcedure.close();
			
			Statement select = connection.createStatement();
			ResultSet rs= select.executeQuery("select DIRECTORY_PATH from all_directories where directory_name = 'UTL_FILE_DIR'");
			if( rs.next())
			{
				String path = rs.getString(1);
				if( path.contains("/")) path = path + "/";
				else path = path + "\\";
				return path + fileNameWithoutPath;
			}
			rs.close();
			select.close();
		} catch (SQLException ex)
		{
			Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			ex.printStackTrace();
		}
		return "";
	}
}
