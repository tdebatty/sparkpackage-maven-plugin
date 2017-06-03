/*
 * The MIT License
 *
 * Copyright 2017 tibo.
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
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;

/**
 *
 * @author tibo
 */
public abstract class MojoTestCase extends AbstractMojoTestCase {

    /**
     * Create an instance of Mojo corresponding to the provided goal.
     * The Mojo will be configured according to values provided in the pom.
     * @param pom
     * @param goal
     * @return
     * @throws Exception if something went wrong...
     */
    public final Mojo lookupConfiguredMojo(final File pom, final String goal)
            throws Exception {

        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        MavenExecutionResult result = new DefaultMavenExecutionResult();

        // populate default values for (a.o.) repository basedir and
        // remote repos
        MavenExecutionRequestPopulator populator =
                getContainer().lookup(MavenExecutionRequestPopulator.class);
        populator.populateDefaults(request);

        // this is needed to allow java profiles to get resolved;
        // i.e. avoid during project builds:
        // [ERROR] Failed to determine Java version for profile
        // java-1.5-detected
        request.setSystemProperties(System.getProperties());

        // and this is needed so that the repo session in the maven session
        // has a repo manager, and it points at the local repo
        // (cf MavenRepositorySystemUtils.newSession() which is what is
        // otherwise done)
        DefaultMaven maven = (DefaultMaven) getContainer().lookup(
                Maven.class);
        DefaultRepositorySystemSession repo_session
                = (DefaultRepositorySystemSession)
                maven.newRepositorySession(request);
        repo_session.setLocalRepositoryManager(
                new SimpleLocalRepositoryManagerFactory().newInstance(
                        repo_session,
                        new LocalRepository(
                                request.getLocalRepository().getBasedir())));

        @SuppressWarnings("deprecation")
        MavenSession session = new MavenSession(
                getContainer(),
                repo_session,
                request,
                result);

        ProjectBuildingRequest building_request =
                session.getProjectBuildingRequest();
        ProjectBuilder project_builder = lookup(ProjectBuilder.class);
        MavenProject project = project_builder.build(pom, building_request)
                .getProject();

        return super.lookupConfiguredMojo(project, goal);

    }

}
