package org.jenkinsci.plugins.orgfolder.github;

import hudson.model.Item;
import jenkins.branch.Branch;
import jenkins.branch.OrganizationFolder;
import jenkins.scm.api.SCMNavigator;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMNavigator;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;

/**
 * Pattern matching on GitHub organization folder member {@link Item}s.
 *
 * @author Kohsuke Kawaguchi
 */
class Sniffer {
    static class OrgMatch {
        final OrganizationFolder folder;
        final GitHubSCMNavigator scm;

        public OrgMatch(OrganizationFolder folder, GitHubSCMNavigator scm) {
            this.folder = folder;
            this.scm = scm;
        }
    }

    public static OrgMatch matchOrg(Object item) {
        if (item instanceof OrganizationFolder) {
            OrganizationFolder of = (OrganizationFolder)item;
            if (of.getNavigators().size()>0) {
                SCMNavigator n = of.getNavigators().get(0);
                if (n instanceof GitHubSCMNavigator) {
                    return new OrgMatch(of, (GitHubSCMNavigator) n);
                }
            }
        }
        return null;
    }

    static class RepoMatch extends OrgMatch {
        final WorkflowMultiBranchProject repo;

        public RepoMatch(OrgMatch x, WorkflowMultiBranchProject repo) {
            super(x.folder,x.scm);
            this.repo = repo;
        }
    }

    public static RepoMatch matchRepo(Object item) {
        if (item instanceof WorkflowMultiBranchProject) {
            WorkflowMultiBranchProject repo = (WorkflowMultiBranchProject)item;
            OrgMatch org = matchOrg(repo.getParent());
            if (org!=null)
                return new RepoMatch(org, repo);
        }
        return null;
    }

    static class BranchMatch extends RepoMatch {
        final WorkflowJob branch;

        public BranchMatch(RepoMatch x, WorkflowJob branch) {
            super(x,x.repo);
            this.branch = branch;
        }

        public Branch getScmBranch() {
            return repo.getProjectFactory().getBranch(branch);
        }
    }

    public static BranchMatch matchBranch(Item item) {
        if (item instanceof WorkflowJob) {
            WorkflowJob branch = (WorkflowJob)item;
            RepoMatch x = matchRepo(item.getParent());
            if (x!=null)
                return new BranchMatch(x,branch);
        }
        return null;
    }
}
