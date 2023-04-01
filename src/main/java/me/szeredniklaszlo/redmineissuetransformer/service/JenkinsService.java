package me.szeredniklaszlo.redmineissuetransformer.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.szeredniklaszlo.redmineissuetransformer.util.RedmineExporter;

@Service
@RequiredArgsConstructor
@Slf4j
public class JenkinsService
{
	private final JiraRestClient jiraRestClient;
	private final RedmineExporter redmineExporter;

	public List<Issue> fetchAllIssuesOrderedById() {
		String jql = "ORDER BY ID";
		int pageSize = 1000;
		int startAt = 0;
		Set<String> fields = Set.of("project", "issuetype", "status", "priority", "summary", "reporter", "assignee",
			"created", "duedate", "updated");

		SearchResult initialSearch = jiraRestClient.getSearchClient().searchJql(jql, pageSize, startAt, fields).claim();
		int totalPages = (int) Math.ceil((double) initialSearch.getTotal() / pageSize);
		log.info("Found {} issues on {} pages (page size: {})", initialSearch.getTotal(), totalPages, pageSize);

		Stream<Issue> issues = StreamSupport.stream(initialSearch.getIssues().spliterator(), false);
		log.info("Fetching issues from Jira: {} / {}", Math.min(initialSearch.getTotal(), pageSize), initialSearch.getTotal());

		for (int page = 1; page < totalPages; page++) {
			Iterable<Issue> fetchedIssues = jiraRestClient.getSearchClient()
				.searchJql(jql, pageSize, page * pageSize, fields)
				.claim().getIssues();
			log.info(
				"Fetching issues from Jira: {} / {}",
				Math.min((page + 1) * pageSize, initialSearch.getTotal()),
				initialSearch.getTotal()
			);

			issues = Stream.concat(
				issues,
				StreamSupport.stream(fetchedIssues.spliterator(), false)
			);
		}

		log.info("Successfully fetched all issues from JIRA. Total issues: {}.", initialSearch.getTotal());
		return issues.toList();
	}
}
