/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.ifc;

import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author Mitch
 */
public interface IStructureProcessor
{
	/**
	 * Apply a Cheshire script to a list of records
	 *
	 * @param tableName		table to update
	 * @param templateStructure	structure to use with the script
	 * @param IDs ArrayList of compound IDs
	 * @param script Cheshire script
	 * @param sqlToRunScript
	 * @param idFieldName
	 * @param conn open JDBC connection
	 * @param errMsgs used to output errors
	 * @return true when successful
	 */
	public boolean processUsingCheshire( String tableName,
			String templateStructure, ArrayList<String> IDs,
			String script, String sqlToRunScript, String idFieldName,
			Connection conn, StringBuilder errMsgs);

	public boolean initCheshire( String cheshireInitSQL,
			Connection conn, StringBuilder errorMsgs);

	public abstract boolean runCheshireMoleculeLoadingSQL(String targetMolString,
			String baseCheshireLoadingSQL, Connection conn, StringBuilder errorMessages,
			String cheshireTask);

	public abstract boolean runGeneralCheshireSQL( String cheshireLoadingSQL,
			Connection conn, StringBuilder errorMessages, java.lang.String cheshireTask);

}
