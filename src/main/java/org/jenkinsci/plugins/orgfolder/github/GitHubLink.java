package org.jenkinsci.plugins.orgfolder.github;

import hudson.model.Action;

import java.net.URL;

/**
 * Link to GitHub
 *
 * @author Kohsuke Kawaguchi
 */
public class GitHubLink implements Action {
    /**
     * Maps to the directory name under webapp/images
     */
    private final String image;

    /**
     * Target of the hyperlink to take the user to.
     */
    private final String url;

    /*package*/ GitHubLink(String image, String url) {
        this.image = image;
        this.url = url;
    }

    /*package*/ GitHubLink(String image, URL url) {
        this(image,url.toExternalForm());
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/github-organization-folder/images/"+ image +"/24x24.png";
    }

    @Override
    public String getDisplayName() {
        return "GitHub";
    }

    @Override
    public String getUrlName() {
        return url;
    }
}
