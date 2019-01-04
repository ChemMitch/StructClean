/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.objects.impl;

import com.thinkscience.demo.structurecleaner.objects.ifc.IFieldData;

/**
 *
 * @author Mitch
 */
public class FieldData implements IFieldData

{

	private String _fieldName;
	private Object _fieldValue;
	private int _decimals;

	public FieldData(String fieldName, Object fieldValue)
	{
		_fieldName = fieldName;
		_fieldValue = fieldValue;
	}

	public FieldData()
	{
	}

	/**
	 * @return the _fieldName
	 */
	public String getFieldName()
	{
		return _fieldName;
	}

	/**
	 * @param fieldName the _fieldName to set
	 */
	public void setFieldName(String fieldName)
	{
		this._fieldName = fieldName;
	}

	/**
	 * @return the _fieldValue
	 */
	public Object getFieldValue()
	{
		return _fieldValue;
	}

	/**
	 * @param fieldValue the _fieldValue to set
	 */
	public void setFieldValue(Object fieldValue)
	{
		this._fieldValue = fieldValue;
	}

	/**
	 * @return the _decimals
	 */
	public int getDecimals()
	{
		return _decimals;
	}

	/**
	 * @param decimals the number of decimal places to use
	 */
	public void setDecimals(int decimals)
	{
		this._decimals = decimals;
	}
}
