package com.prairiegrade.webhook.security.basicauth;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a flat list of tokens from {@value #FILENAME}, one token per line.  These
 * are treated as passwords.  Usernames are ignored altogether.
 */
public class TokenListAuthService implements UsernamePasswordAuthService {
	private static final Logger logger = LoggerFactory.getLogger(TokenListAuthService.class);
	public static final String FILENAME = "api-tokens.txt";
	private final List<String> tokens;

	public TokenListAuthService(String classpathResource) {
		InputStream in = TokenListAuthService.class.getClassLoader().getResourceAsStream(classpathResource);
		if (in != null) {
			tokens = load(in);
			logger.info("{} tokens from {} loaded", tokens.size(), classpathResource);
		} else {
			logger.info("{} not found, token based auth disabled.", classpathResource);
			tokens = Arrays.asList();
		}
	}
	
	public TokenListAuthService() {
		this(FILENAME);
	}

	static List<String> load(InputStream in) {
		List<String> tokenList = new ArrayList<>();
		try (Scanner scanner = new Scanner(in)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (!line.startsWith("#") && !line.trim().isEmpty()) {
					tokenList.add(line.trim());
				}
			}
		}
		return tokenList;
	}

	@Override
	public boolean isValid(String username, String password) {
		return tokens.contains(password);
	}
}
