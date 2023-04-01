package me.szeredniklaszlo.redmineissuetransformer;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.atlassian.jira.rest.client.api.domain.Issue;

import lombok.RequiredArgsConstructor;
import me.szeredniklaszlo.redmineissuetransformer.service.JenkinsService;
import me.szeredniklaszlo.redmineissuetransformer.util.RedmineExporter;

@SpringBootApplication
@RequiredArgsConstructor
public class RedmineIssueTransformerApplication implements CommandLineRunner {

	private final JenkinsService jenkinsService;
	private final RedmineExporter redmineExporter;

	public static void main(String[] args) {
		SpringApplication.run(RedmineIssueTransformerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<Issue> issues = jenkinsService.fetchAllIssuesOrderedById();
		redmineExporter.exportToCsv(issues, "out.csv");
	}
}
