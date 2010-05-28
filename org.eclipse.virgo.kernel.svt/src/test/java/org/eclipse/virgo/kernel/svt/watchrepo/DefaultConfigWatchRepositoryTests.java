/*******************************************************************************
 * Copyright (c) 2008, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   VMware Inc. - initial contribution
 *******************************************************************************/

package org.eclipse.virgo.kernel.svt.watchrepo;

import static org.junit.Assert.assertEquals;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.virgo.kernel.svt.AbstractKernelTests;

public class DefaultConfigWatchRepositoryTests extends AbstractKernelTests {

	@BeforeClass
	public static void doKernelStartUp() throws Exception {
		configureWatchRepoWithDefaultConfiguration(getKernelConfigDir());
		new Thread(new KernelStartUpThread()).start();
		AbstractKernelTests
				.waitForKernelStartFully("service:jmx:rmi:///jndi/rmi://localhost:9875/jmxrmi");
		addBundlesToWatchedRepository(getWatchedRepoDir());
		Thread.sleep(2000);
	}

	@Test
	public void testKernelStartUpStatus() throws Exception {
		assertEquals(STATUS_STARTED, getKernelStartUpStatus());
	}

	@Test
	public void testWatchRepoWithDefaultConfiguraton() throws Exception {
		String repoName = (String) getMBeanServerConnection()
				.getAttribute(
						new ObjectName(
								"org.eclipse.virgo.kernel:type=Repository,name=watched-repository"),
						"Name");
		assertEquals("watched-repository", repoName);
		CompositeData[] object =  (CompositeData[]) getMBeanServerConnection()
				.getAttribute(
						new ObjectName(
								"org.eclipse.virgo.kernel:type=Repository,name=watched-repository"),
						"AllArtifactDescriptorSummaries");

		for(int i=0;i<object.length;i++){
		assertEquals("org.springframework.dmServer.testtool.incoho", object[i]
				.get("name"));
		assertEquals(
				"1.0.0.RELEASE",object[i].get("version"));
		assertEquals("bundle", object[i].get("type"));
		}
	}

	@AfterClass
	public static void doKernelShutdown() throws Exception {
		
		new Thread(new KernelShutdownThread()).start();
		AbstractKernelTests
				.waitForKernelShutdownFully("service:jmx:rmi:///jndi/rmi://localhost:9875/jmxrmi");
	}

}
