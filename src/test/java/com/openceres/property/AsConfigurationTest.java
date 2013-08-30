package com.openceres.property;

import org.junit.Test;

import com.openceres.config.AsConfiguration;

public class AsConfigurationTest {

	@Test
	public void testUpload() {
		AsConfiguration asConfiguration = AsConfiguration.getInstance();
		asConfiguration.startup();
		
		try {
			asConfiguration.upload();
			
			while(true)
			{
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testDownload() {
		AsConfiguration asConfiguration = AsConfiguration.getInstance();
		asConfiguration.startup();
		
		try {
			asConfiguration.download();
			System.out.println(asConfiguration.get("global.service.db"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
