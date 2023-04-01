package me.szeredniklaszlo.redmineissuetransformer.model;

import org.joda.time.LocalDate;

import com.atlassian.jira.rest.client.api.domain.Issue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedmineIssue {
	String project;
	String tracker;
	String status;
	String priority;
	String subject;
	String author;
	String assignee;
	String category;
	String targetVersion;
	LocalDate startDate;
	LocalDate dueDate;
	Integer estimatedTime;
	String relatedIssues;
	boolean isPrivate;

	public static RedmineIssue fromJiraIssue(Issue issue) {
		return RedmineIssue.builder()
			.project(issue.getProject().getName())
			.tracker(issue.getIssueType().getName())
			.status(issue.getStatus().getName())
			.priority(issue.getPriority() == null ? null : issue.getPriority().getName())
			.subject(issue.getSummary())
			.author(issue.getReporter() == null ? null : issue.getReporter().getDisplayName())
			.assignee(issue.getAssignee() == null ? null : issue.getAssignee().getDisplayName())
			.category(null)
			.targetVersion(null)
			.startDate(issue.getCreationDate().toLocalDate())
			.dueDate(issue.getDueDate() == null ? null : issue.getDueDate().toLocalDate())
			.estimatedTime(issue.getTimeTracking() == null ? null : issue.getTimeTracking().getOriginalEstimateMinutes())
			.relatedIssues(null)
			.isPrivate(false)
			.build();
	}
}