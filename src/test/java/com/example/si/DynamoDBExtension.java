package com.example.si;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DynamoDBExtension implements BeforeAllCallback, AfterAllCallback {

	public static final String LIBRARY_PATH_SQLITE = "sqlite4java.library.path";
	public static final String SQLITE_NATIVE_LIBS = "native-libs";

	private DynamoDBProxyServer proxyServer;

	public DynamoDBExtension() {
		System.setProperty(LIBRARY_PATH_SQLITE, SQLITE_NATIVE_LIBS);
	}

	@Override
	public void afterAll(ExtensionContext extensionContext) throws Exception {
		proxyServer.stop();
	}

	@Override
	public void beforeAll(ExtensionContext extensionContext) throws Exception {
		proxyServer = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", "8000"});
		proxyServer.start();
	}
}
