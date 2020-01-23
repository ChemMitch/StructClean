/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.ifc;

/**
 *
 * @author Mitch
 */
public interface IOverlaidStructureData {

		public String getMolName();
		public void setMolName(String molName);
		public byte[] getImageData();
		public void setImageData(byte[] imageData);
		public double getMolWt();
		public void setMolWt(double molWt);
		public String getMolFMLA();
		public void setMolFMLA(String molFMLA);
		public String getId();
		public void setId(String id);
		public String getChimeStringAfter();
		public String getChimeStringAfter2();
		public void setChimeStringAfter(String _chimeStringAfter);
		public void setChimeStringAfter2(String _chimeStringAfter);
		public String getChimeStringBefore();
		public void setChimeStringBefore(String _chimeStringBefore);
		
}
