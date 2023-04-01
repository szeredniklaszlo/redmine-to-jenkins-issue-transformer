package me.szeredniklaszlo.redmineissuetransformer.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.domain.Issue;

import lombok.extern.slf4j.Slf4j;
import me.szeredniklaszlo.redmineissuetransformer.model.RedmineIssue;

@Component
@Slf4j
public class RedmineExporter {
	private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
	private final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH.mm");

	public void exportToCsv(List<Issue> issues, String outputFile) throws IOException {
		CSVFormat csvFormat = CSVFormat.Builder.create()
			.setQuoteMode(QuoteMode.ALL_NON_NULL)
			.setQuote('"')
			.build();

		try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputFile, StandardCharsets.UTF_8), csvFormat)) {
			issues.forEach(issue -> {
				RedmineIssue redmineIssue = RedmineIssue.fromJiraIssue(issue);

				try {
					csvPrinter.printRecord(
						redmineIssue.getProject(),
						redmineIssue.getTracker(),
						redmineIssue.getStatus(),
						redmineIssue.getPriority(),
						redmineIssue.getSubject(),
						redmineIssue.getAuthor(),
						redmineIssue.getAssignee(),
						redmineIssue.getCategory(),
						redmineIssue.getTargetVersion(),
						dateFormatter.print(redmineIssue.getStartDate()),
						redmineIssue.getDueDate() == null ? null : dateFormatter.print(redmineIssue.getDueDate()),
						minutesIntoLocalTimeString(redmineIssue.getEstimatedTime()),
						redmineIssue.getRelatedIssues(),
						redmineIssue.isPrivate() ? "Yes" : "No"
					);
				} catch (IOException e) {
					log.error("Could not write CSV file: {}", outputFile);
				}
			});
		}

		log.info("{} issues have been exported in Redmine format to {}.", issues.size(), outputFile);
	}

	private String minutesIntoLocalTimeString(Integer minutes) {
		if (minutes == null) {
			return null;
		}

		LocalTime time = LocalTime.MIDNIGHT.plusMinutes(minutes);
		return timeFormatter.print(time);
	}
}
