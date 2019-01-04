/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.ifc;

/**
 * Field returned from a database query
 * @author Mitch
 */
public interface IReturnedField
{
	public String getTableName();
	public void setTableName(String newTableName);
	public String getFieldName();
	public void setFieldName(String newFieldName);

}
