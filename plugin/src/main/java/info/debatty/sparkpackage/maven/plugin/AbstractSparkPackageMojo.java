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
package info.debatty.sparkpackage.maven.plugin;

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

    @Parameter(defaultValue = "${project.version}")
    protected String version = "";
    protected String organization;
    protected String repo;
    protected String jar_path;
    protected String zip_path;

    public MavenProject getProject() {
        return project;
    }

    @Override
    public void execute() throws MojoFailureException {

        //version = project.getVersion();
        getLog().info("Project version: " + version);

        parseOranizationAndRepo();

        jar_path = project.getBuild().getDirectory() + "/"
                + project.getArtifactId() + "-" + version + ".jar";
        File file = new File(jar_path);
        if (!file.exists()) {
            throw new MojoFailureException(
                    "Jar file " + jar_path + " not found!");
        }
        getLog().info("JAR file: " + jar_path);

        zip_path = project.getBuild().getDirectory() + "/" + repo
                + "-" + version + ".zip";
        getLog().info("ZIP file: " + zip_path);

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
}
