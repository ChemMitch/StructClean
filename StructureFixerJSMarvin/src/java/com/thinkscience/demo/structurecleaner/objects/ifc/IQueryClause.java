/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.ifc;

/**
 *
 * @author Mitch
 */
public interface IQueryClause
{
	public String getTableName();
	public void setTableName(String newTableName);
	public String getFieldName();
	public void setFieldName(String newFieldName);
	public String getFieldOperator();
	public void setFieldOperator(String newOperator);
	public String getFieldQueryValue();
	public void setFieldQueryValue(String newValue);
	public void setConjunction(String newConjunction);
	public String  getConjunction();
}
