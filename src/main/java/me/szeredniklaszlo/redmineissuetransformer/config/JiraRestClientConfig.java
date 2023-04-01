package me.szeredniklaszlo.redmineissuetransformer.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import lombok.SneakyThrows;

@Configuration
public class JiraRestClientConfig {
	@Value("${jira.rest.client.uri}")
	private String jiraRestClientURI;

	@Bean
	@SneakyThrows
	public JiraRestClient jiraRestClient() {
		return new AsynchronousJiraRestClientFactory()
			.create(new URI(jiraRestClientURI), new AnonymousAuthenticationHandler());
	}
}
