package com.prairiegrade.webhook.util;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContext;

import org.junit.Test;

import com.prairiegrade.webhook.util.WebhookUtils;

public class WebhookUtilsTest {

	@Test
	public void testReadFromClasspath() {
		String actual = WebhookUtils.readFromClasspath("testfile.txt");
		assertEquals("hello world\r\ngoodbye world", actual);
	}

	@Test
	public void testGetVersionString() {
		ServletContext servletContext = mock(ServletContext.class);
		expect(servletContext.getResourceAsStream(eq("/META-INF/MANIFEST.MF")))
				.andReturn(getClass().getClassLoader().getResourceAsStream("FAKEMANIFEST.MF"))
				.once();
		replay(servletContext);
		
		String actual = WebhookUtils.getVersionString(servletContext);
		assertEquals("1.0-SNAPSHOT", actual);
		
		verify(servletContext);
	}
	
	@Test
	public void testGetVersionStringSansManifest() {
		ServletContext servletContext = mock(ServletContext.class);
		expect(servletContext.getResourceAsStream(eq("/META-INF/MANIFEST.MF")))
				.andReturn(null)
				.once();
		replay(servletContext);
		
		String actual = WebhookUtils.getVersionString(servletContext);
		assertEquals(null, actual);
		verify(servletContext);
	}

}
