/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.impl;

import com.thinkscience.demo.structurecleaner.objects.ifc.IReturnedField;

/**
 *
 * @author Mitch
 */
public class ReturnedField implements IReturnedField
{
	public ReturnedField( String newTableName, String newFieldName)
	{
		_tableName = newTableName;
		_fieldName =newFieldName;
	}
	public ReturnedField()
	{}

	public String getTableName()
	{
		return _tableName;
	}

	public void setTableName(String newTableName)
	{
		_tableName = newTableName;
	}

	public String getFieldName()
	{
		return _fieldName;
	}
	public void setFieldName(String newFieldName)
	{
		_fieldName = newFieldName;
	}

	public boolean equals(Object aThat)
	{
		if ( this == aThat ) return true;
		if ( !(aThat instanceof ReturnedField) ) return false;
		ReturnedField that = (ReturnedField)aThat;
		return
			this.getFieldName().equalsIgnoreCase(that.getFieldName())
			&& this.getTableName().equalsIgnoreCase(that.getTableName());
	}
	public int hashCode()
	{
		int ret = _tableName.hashCode() *1000000 + _fieldName.hashCode();
		return ret;
	}
	private String _tableName;
	private String _fieldName;

}
