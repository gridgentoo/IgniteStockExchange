/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.tools.jirabranches;

import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.async.*;
import org.apache.ignite.internal.util.typedef.*;
import org.apache.ignite.internal.util.typedef.internal.*;

import java.awt.*;
import java.io.*;
import java.lang.ProcessBuilder.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;

/**
 * Generates html page with the JIRA tickets information.
 */
public class JiraBranchesToHtml {
    /** */
    public static final String SCRIPT_PATH = U.getIgniteHome() + "/scripts/jira-branches.sh";

    /** */
    public static final String INPUT_FILE = U.getIgniteHome() + "/scripts/jira-branches.js";

    /** */
    public static final String OUTPUT_FILE = U.getIgniteHome() + "/scripts/jira-branches-results.html";

    /** */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    /** */
    private static final Pattern TICKET_PATTERN = Pattern.compile("\\d{6}|\\d{5}|\\d{4}|\\d{3}|\\d{2}");

    /** */
    public static final Comparator<Result> COMP = new Comparator<Result>() {
        @Override public int compare(Result o1, Result o2) {
            if (o1.issue != null && o2.issue != null) {
                int res = name(o1.issue.getAssignee()).compareTo(name(o2.issue.getAssignee()));

                return res == 0 ? o1.issueKey.compareTo(o2.issueKey) : res;
            }
            else if (o1.issue == null && o2.issue == null)
                return o1.issueKey.compareTo(o2.issueKey);
            else
                return o1.issue == null ? 1 : -1;
        }
    };

    /**
     * @param args Arguments.
     * @throws Exception If failed.
     */
    public static void main(String[] args) throws Exception {
        System.out.print("Report 'Closed' issues only [y/N]: ");

        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));

        boolean closedOnly = "y".equalsIgnoreCase(rdr.readLine());

        List<String> branches = getBranches();

        Credentials cred = askForJiraCredentials("https://issues.apache.org/jira");

        List<Result> res = new ArrayList<>();

        try (JiraRestClient restClient = new AsynchronousJiraRestClientFactory().
            createWithBasicHttpAuthentication(URI.create(cred.jiraUrl), cred.name, cred.pswd)) {
            for (String branchName : branches) {
                if (branchName.toLowerCase().startsWith("IGNITE".toLowerCase())) {
                    Result r = result(restClient, branchName, cred.jiraUrl);

                    if (r.issue == null || !closedOnly || "Closed".equalsIgnoreCase(r.issue.getStatus().getName())) {
                        System.out.println("Added issue: " + r);

                        res.add(r);
                    }
                }
            }
        }

        String s = buildHtmlReport(res);

        writeToOutputFileAndOpen(s);
    }

    /**
     * @param content Content for file.
     */
    public static void writeToOutputFileAndOpen(String content) throws IOException {
        System.out.println(content);

        try (OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE))) {
            bw.write(content);
        }

        if (Desktop.isDesktopSupported())
            Desktop.getDesktop().open(new File(OUTPUT_FILE));
        else
            System.out.println("Results have been written to: " + OUTPUT_FILE);
    }

    /**
     * @param jiraUrl Jira URL.
     * @return Credentials/
     * @throws Exception If failed.
     */
    public static Credentials askForJiraCredentials(String jiraUrl) throws Exception{
        System.out.println("Need to enter credentials for JIRA [" + jiraUrl + "]");
        System.out.print("JIRA user: ");

        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));

        String user = rdr.readLine();

        if (F.isEmpty(user))
            throw new IllegalStateException("JIRA user name cannot be empty.");

        // Mac OS has no Console support (at least in IDE), so have to read password as plain text.
        System.out.print("Password: ");

        String pswd = rdr.readLine();

        if (F.isEmpty(pswd))
            throw new IllegalStateException("JIRA password cannot be empty.");

        return new Credentials(jiraUrl, user, pswd);
    }

    /**
     * @return All branches from origin repository.
     * @throws Exception If failed.
     */
    public static List<String> getBranches() throws Exception {
        return getBranches0("");
    }

    /**
     * @return All branches from origin repository.
     * @throws Exception if failed.
     */
    public static List<String> getBranches0(String gitHome) throws Exception {
        System.out.println();
        System.out.println(">>> Executing script: " + SCRIPT_PATH);
        System.out.println();

        Process proc = new ProcessBuilder(SCRIPT_PATH, gitHome)
            .directory(new File(SCRIPT_PATH).getParentFile())
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start();

        proc.waitFor();

        System.out.println();
        System.out.println(">>> Finished executing script [script=" + SCRIPT_PATH +
            ", exitCode=" + proc.exitValue() + ']');
        System.out.println();

        if (proc.exitValue() != 0)
            throw new Exception("Failed to run script [script=" + SCRIPT_PATH +
                ", exitCode=" + proc.exitValue() + ']');

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(INPUT_FILE)))) {
            List<String> branches = new ArrayList<>();

            for (String line; (line = br.readLine()) != null; ) {
                String branch = line.replace("\\", "").trim();

                if (branch.startsWith("remotes/origin/"))
                    branches.add(branch.substring("remotes/origin/".length()));
            }

            return branches;
        }
    }

    /**
     * @param restClient Rest client.
     * @param branchName Branch name.
     * @param jiraUrl
     * @return Result.
     */
    public static Result result(JiraRestClient restClient, String branchName, String jiraUrl) {
        // Look for digits at branch name.
        Matcher m = TICKET_PATTERN.matcher(branchName);

        if (!m.find())
            return new Result(branchName, null, "Unknown branch name pattern.", jiraUrl);

        String digits = m.group(0);

        int lastJiraKeyIdx = branchName.indexOf(digits) + digits.length();

        if (lastJiraKeyIdx + 1 == branchName.length()) {
            // For branches like "IGNITE-xxx"
            try {
                Issue issue = restClient.getIssueClient().getIssue(branchName).claim();

                return new Result(branchName, issue, null, jiraUrl);
            }
            catch (RestClientException e) {
                return new Result(branchName, null, e.getMessage(), jiraUrl);
            }
        }
        else {
            // For branches like "IGNITE-xxx-<description>".
            try {
                String jiraKey = branchName.substring(0, lastJiraKeyIdx);

                Issue issue = restClient.getIssueClient().getIssue(jiraKey).claim();

                return new Result(branchName, issue, null, jiraUrl);
            }
            catch (RestClientException e) {
                return new Result(branchName, null, e.getMessage(), jiraUrl);
            }
        }
    }

    /**
     * @param res Results.
     * @return Output.
     */
    public static String buildHtmlReport(List<Result> res) {
        StringBuilder sb = new StringBuilder();

        println(sb, "<html>\n<head></head>\n<body>");
        print(sb, "<table>");

        Collections.sort(res, COMP);

        for (Result r : res)
            printResult(sb, r);

        print(sb, "\n</table>");
        print(sb, "\n</body>\n</html>\n");

        return sb.toString();
    }

    /**
     * @param sb String builder.
     * @param r Result.
     */
    public static void printResult(StringBuilder sb, Result r) {
        print(sb, "\n<tr>");

        if (r.error != null) {
            print(sb, "<th colspan=7 align=\"left\">" + r.issueKey + " " + r.error + "</th>");
            print(sb, "</tr>");

            return;
        }

        print(sb, "<th colspan=7 align=\"left\">" +
            "<a href=" + URI.create(r.jiraUrl) + "/browse/" + r.issue.getKey() + ">" +
            r.issueKey + ' ' + r.issue.getSummary() + "<a></th>");

        print(sb, "</tr><tr>");

        print(sb, "<td><strong>Assignee:</strong> " + name(r.issue.getAssignee()) + "</td>");
        print(sb, "<td><strong>Reporter:</strong> " + name(r.issue.getReporter()) + "</td>");
        print(sb, "<td><strong>Status:</strong> " + r.issue.getStatus().getName() + "</td>");
        print(sb, "<td><strong>Resolution:</strong> " +
            (r.issue.getResolution() == null ? "Unresolved" : r.issue.getResolution().getName()) + "</td>");

        if (r.issue.getFixVersions() != null) {
            print(sb, "<td><strong>Version:</strong>");

            for (Version version : r.issue.getFixVersions())
                print(sb, " " + version.getName());

            print(sb, "</td>");
        }

        print(sb, "<td><strong>CreationDate:</strong> " + FORMAT.format(r.issue.getCreationDate().toDate()) + "</td>");
        print(sb, "<td><strong>UpdateDate:</strong> " + FORMAT.format(r.issue.getUpdateDate().toDate()) + "</td>");

        print(sb, "</tr><tr><td>  </td></tr>");
    }

    /**
     * @param sb StringBuilder.
     * @param s String to print.
     */
    public static void print(StringBuilder sb, String s) {
        sb.append(s);
    }

    /**
     * @param sb StringBuilder.
     * @param s String to print.
     */
    public static void println(StringBuilder sb, String s) {
        sb.append(s).append(System.getProperty("line.separator"));
    }

    /**
     * @param user User.
     * @return Name.
     */
    private static String name(BasicUser user) {
        if (user == null)
            return "";

        return user.getDisplayName() == null ? (user.getName() == null ? "" : user.getName()) : user.getDisplayName();
    }

    /** */
    public static class Result {
        /** */
        public final String issueKey;

        /** */
        public final Issue issue;

        /** */
        public final String error;

        /** */
        public final String jiraUrl;

        /** */
        public Result(String issueKey, Issue issue, String error, String url) {
            this.issueKey = issueKey;
            this.issue = issue;
            this.error = error;
            jiraUrl = url;
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return "Result [" +
                "issueKey='" + issueKey + '\'' +
                ", issue=" + (issue == null ? "" : issue.getKey() + " " + issue.getSummary()) +
                ", error='" + error + '\'' +
                ']';
        }
    }

    /** */
    public static class Credentials {
        /** */
        public final String jiraUrl;

        /** */
        public final String name;

        /** */
        public final String pswd;

        /** */
        Credentials(String jiraUrl, String name, String pswd) {
            this.jiraUrl = jiraUrl;
            this.name = name;
            this.pswd = pswd;
        }
    }
}
