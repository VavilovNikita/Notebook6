package ru.vavilov.notebook6.codesage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GitHubPullRequestService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<GitFileChange> getPullRequestChanges(String prUrl) {
        String[] parts = prUrl.split("/");
        if (parts.length < 7 || !"pull".equals(parts[5])) {
            throw new IllegalArgumentException("Неверный формат ссылки на Pull Request");
        }

        String owner = parts[3];
        String repo = parts[4];
        String prNumber = parts[6];

        String apiUrl = String.format("https://api.github.com/repos/%s/%s/pulls/%s/files", owner, repo, prNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GitFileChange[]> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.GET,
            request,
            GitFileChange[].class
        );

        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public String getCodeFromPullRequest(String prUrl) {
        List<GitFileChange> changes = getPullRequestChanges(prUrl);

        StringBuilder code = new StringBuilder();

        for (GitFileChange change : changes) {
            code.append("// --- File: ").append(change.filename).append("\n");
            if (change.patch != null) {
                code.append(change.patch).append("\n\n");
            }
        }

        return code.toString();
    }

    public static class GitFileChange {
        public String filename;
        public String status;
        public String patch;
    }
}
