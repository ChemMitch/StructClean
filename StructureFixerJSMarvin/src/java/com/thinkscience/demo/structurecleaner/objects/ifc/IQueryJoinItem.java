/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.objects.ifc;

/**
 *
 * @author Mitch
 */
public interface IQueryJoinItem
{
	public String getLeftFieldName();
	public String getRightFieldName();
	public void setRightFieldName(String newValue);
	public void setLeftFieldName(String newValue);
	public String getLeftTableRerence();
	public String getRightTableRerence();
	public void setRightTableRerence(String newValue);
	public void setLeftTableRerence(String newValue);

}
