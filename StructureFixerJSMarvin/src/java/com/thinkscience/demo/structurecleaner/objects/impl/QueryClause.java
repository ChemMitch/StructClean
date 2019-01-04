/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.impl;

import com.thinkscience.demo.structurecleaner.objects.ifc.IQueryClause;

/**
 *
 * @author Mitch
 */
public class QueryClause implements IQueryClause
{

	public static String DEFAULT_CONJUNCTION = "AND";

	public QueryClause( String newTableName, String newFieldName, String newFieldOperator,
			String newFieldQueryValue, String newConjunction)
	{
		_tableName = newTableName;
		_fieldName = newFieldName;
		_fieldOperator = newFieldOperator;
		_fieldQueryValue = newFieldQueryValue;
		_conjunction = newConjunction;
	}

	public String getFieldName()
	{
		return _fieldName;
	}
	public void setFieldName(String newFieldName)
	{
		_fieldName = newFieldName;
	}

	public String getFieldOperator()
	{
		return _fieldOperator;
	}

	public void setFieldOperator(String newOperator)
	{
		_fieldOperator = newOperator;
	}

	public String getFieldQueryValue()
	{
		return _fieldQueryValue;
	}

	public void setFieldQueryValue(String newValue)
	{
		_fieldQueryValue = newValue;
	}

	public String getConjunction()
	{
		return _conjunction;
	}

	public void setConjunction(String newConjunction)
	{
		_conjunction = newConjunction;
	}

	public String getTableName()
	{
		return _tableName;
	}

	public void setTableName(String newTableName)
	{
		_tableName = newTableName;
	}

	private String _tableName;
	private String _fieldName;
	private String _fieldOperator;
	private String _fieldQueryValue;
	private String _conjunction;
}

