/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.objects.ifc;

/**
 *
 * @author Mitch
 */
public interface IFieldData
{

	public String getFieldName();

	public void setFieldName(String fieldName);

	public Object getFieldValue();

	public void setFieldValue(Object fieldValue);

	public int getDecimals();

	public void setDecimals(int decimals);
}
