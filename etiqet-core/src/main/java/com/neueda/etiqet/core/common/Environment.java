package com.neueda.etiqet.core.common;

import com.neueda.etiqet.core.common.exceptions.EnvironmentVariableNotFoundException;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Environment {

	private Environment() {}
	
	/**
	 * Logger attribute
	 */
	private static final Logger LOG = LogManager.getLogger(Environment.class.getName());
	
	/**
	 *
	 * @param key environment variable name
	 * @return whether the environment variable is set
	 */
	public static Boolean isEnvVarSet(String key) {
		return System.getenv(key) != null || System.getProperty(key) != null;
	}
	
	/**
	 *
	 * @param input String containing at least one environment variable
	 * @return string with the environment variable names substituted for their value
	 * @throws EtiqetException when an environment variable isn't found from the input string
	 */
	public static String resolveEnvVars(String input) throws EtiqetException {
		String res = null;

		if (!StringUtils.isNullOrEmpty(input)) {
			// match ${ENV_VAR_NAME} or $ENV_VAR_NAME
            Pattern p = Pattern.compile("\\$\\{([\\w\\.]+)\\}|\\$([\\w\\.]+)");
			Matcher m = p.matcher(input); // create a matcher object
			StringBuffer sb = new StringBuffer();
            resolveEnvVars(m, sb);
			res = sb.toString().replace("/", File.separator);
		}

		return res;
	}

	/**
	 * Resolves Environment Variables in the path provided, including the path
	 * @param path path to the file
	 * @return InputStream of the file with environment variables resolved
	 * @throws EtiqetException when an environment variable isn't found from the input string
	 */
	public static InputStream fileResolveEnvVars(String path) throws EtiqetException {
		InputStream res = null;
		
		if (!StringUtils.isNullOrEmpty(path)) {
			StringBuilder sbr = new StringBuilder();
			path = resolveEnvVars(path);
			try (Stream<String> stream = Files.lines(Paths.get(path))) {

				Iterator<String> str = stream.iterator();
				
				while(str.hasNext()) {
					String str2 = str.next();
					// match ${ENV_VAR_NAME} or $ENV_VAR_NAME
					Pattern p = Pattern.compile(("\\$\\{([\\w\\.]+)\\}|\\$([\\w\\.]+)"));
					Matcher m = p.matcher(str2); // create a matcher object
					StringBuffer sb = new StringBuffer();
                    resolveEnvVars(m, sb);
					sbr.append(sb)
                       .append(System.lineSeparator());
				}
				
				res = new ByteArrayInputStream(sbr.toString().replace("/", File.separator).getBytes(StandardCharsets.UTF_8.name()));

			} catch (IOException e) {
				LOG.error(e);
			}

		}

		return res;
	}

	private static void resolveEnvVars(Matcher m, StringBuffer sb) throws EtiqetException {
        while (m.find()) {
            String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
            String envVarValue = System.getProperty(envVarName);
            if(StringUtils.isNullOrEmpty(envVarValue))
                envVarValue = System.getenv(envVarName);
            if(StringUtils.isNullOrEmpty(envVarValue))
                throw new EnvironmentVariableNotFoundException("Environment variable not found: " + envVarName);
            envVarValue = envVarValue.replace(File.separator,"/");
            m.appendReplacement(sb, envVarValue);
        }
        m.appendTail(sb);
    }

}
