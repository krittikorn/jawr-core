/**
 * Copyright 2008 Jordi Hern�ndez Sell�s
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.jawr.web.resource.bundle.factory.util;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import net.jawr.web.resource.bundle.factory.PropertiesBasedBundlesHandlerFactory;

import org.apache.log4j.Logger;

/**
 * Utility class which can be used to merge several configuration sources. 
 * It merges the properties from a base Properties object with another one. 
 * Mappings, children of composites and other properties are extended rather than overwritten, so 
 * that an existing bundle from the source can get additional mappings.  
 * 
 * @author Jordi Hern�ndez Sell�s
 */
public class ConfigPropertiesAugmenter {
	private final Properties configProperties; 
	private Set privateConfigProperties;
	private static final Logger log = Logger.getLogger(ConfigPropertiesAugmenter.class);

	/**
	 * Creates an instance of the augmenter which uses configProperties as the base configuration to 
	 * augment, but prevents the overwriting of any property stated in the privateConfigProperties 
	 * list. 
	 * 
	 * @param configProperties Base configuration. 
	 * @param privateConfigProperties Set of names of properties which may not be overriden. 
	 */
	public ConfigPropertiesAugmenter(Properties configProperties,
			Set privateConfigProperties) {
		super();
		this.configProperties = configProperties;
		this.privateConfigProperties = privateConfigProperties;
	}

	/**
	 * @param configProperties
	 */
	public ConfigPropertiesAugmenter(final Properties configProperties) {
		super();
		this.configProperties = configProperties;
	}
	
	
	/**
	 * Augments the base configuration with the properties specified as parameter. 
	 * 
	 * @param configToAdd
	 */
	public void augmentConfiguration(Properties configToAdd) {
		for(Iterator it = configToAdd.keySet().iterator();it.hasNext();) {
			String configKey = (String) it.next();
			
			// Skip the property is it is not overridable
			if(null != privateConfigProperties && privateConfigProperties.contains(configKey)) {
				log.warn("The property " + configKey 
						+ " can not be overriden. It will remain with a value of " 
						+ configProperties.get(configKey));
				continue;
			}
			
			// Augment mappings
			if(isAugmentable(configKey)) {
				String currentValue = configProperties.getProperty(configKey);
				currentValue += "," + configToAdd.get(configKey);
				configProperties.put(configKey,currentValue);
			}
			else // replace properties
				configProperties.put(configKey, configToAdd.get(configKey));
		}
	}

	/**
	 * Determine wether a property is augmentable (so instead of overriding, values are appended to 
	 * the current values). 
	 * 
	 * @param configKey
	 * @return
	 */
	protected boolean isAugmentable(String configKey) {
		boolean rets = false;
		rets = (configKey.endsWith(PropertiesBasedBundlesHandlerFactory.BUNDLE_FACTORY_CUSTOM_MAPPINGS) || 		// mappings
				configKey.endsWith(PropertiesBasedBundlesHandlerFactory.BUNDLE_FACTORY_CUSTOM_COMPOSITE_NAMES) || 	// children of composites
				configKey.equals(PropertiesBasedBundlesHandlerFactory.CUSTOM_POSTPROCESSORS + 
								PropertiesBasedBundlesHandlerFactory.CUSTOM_POSTPROCESSORS_NAMES) || 		// Postprocessors definition
				configKey.equals(PropertiesBasedBundlesHandlerFactory.CUSTOM_GENERATORS));					// Generators definition
		
		rets = rets && configProperties.containsKey(configKey);
		return rets;
	}
	

}
