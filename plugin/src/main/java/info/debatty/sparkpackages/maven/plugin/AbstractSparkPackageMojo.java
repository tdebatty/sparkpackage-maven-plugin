/*
 * The MIT License
 *
 * Copyright 2015 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.debatty.sparkpackages.maven.plugin;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author Thibault Debatty
 */
abstract class AbstractSparkPackageMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "url",
        defaultValue = "http://spark-packages.org/api/submit-release")
    private String url;

    /**
     * Github username.
     */
    @Parameter(property = "username", defaultValue = "")
    private String username;

    /**
     * Github Personal Access Token with at least "read:org" permissions.
     * https://help.github.com/articles/creating-a-personal-access-token-for-the
     * -command-line/
     */
    @Parameter(property = "token", defaultValue = "")
    private String token;

    private String version = "";
    private String organization;
    private String repo;
    private String jar_path;
    private String zip_path;

    public final MavenProject getProject() {
        return project;
    }

    public final String getVersion() {
        return version;
    }

    public final String getOrganization() {
        return organization;
    }

    public final String getRepo() {
        return repo;
    }

    public final String getJarPath() {
        return jar_path;
    }

    public final String getZipPath() {
        return zip_path;
    }

    public final String getUsername() {
        return username;
    }

    public final String getToken() {
        return token;
    }



    /**
     * Return the sparkpackages_url (that may be defined in the pom).
     * @return
     */
    public final String getSparkpackagesUrl() {
        return url;
    }

    @Override
    public void execute() throws MojoFailureException {

        parseVersion();
        parseOranizationAndRepo();
        parseJarPath();
        parseZipPath();

        realexe();
    }

    public abstract void realexe() throws MojoFailureException;

    void parseOranizationAndRepo() throws MojoFailureException {
        if (project.getScm() == null) {
            throw new MojoFailureException("Your pom.xml has no scm section!");
        }

        String github_url = project.getScm().getUrl();
        getLog().info("Project github URL: " + github_url);
        Pattern pattern = Pattern.compile("github\\.com:(.+)\\/(.+)\\.git");
        Matcher matcher = pattern.matcher(github_url);
        if (!matcher.find()) {
            throw new MojoFailureException(
                    "Could not find GitHub organization/repo in your "
                            + "versioning URL");
        }
        organization = matcher.group(1);
        repo = matcher.group(2);
    }

    void parseJarPath() throws MojoFailureException {
        jar_path = project.getBuild().getDirectory() + "/"
                + project.getArtifactId() + "-" + version + ".jar";
        File file = new File(jar_path);
        if (!file.exists()) {
            throw new MojoFailureException(
                    "Jar file " + jar_path + " not found!");
        }
        getLog().info("JAR file: " + jar_path);
    }

    void parseVersion() {
        version = project.getVersion();
        getLog().info("Project version: " + version);
    }

    void parseZipPath() {
        zip_path = project.getBuild().getDirectory() + "/" + repo
                + "-" + version + ".zip";
        getLog().info("ZIP file: " + zip_path);
    }
}
