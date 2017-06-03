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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 *
 * @author Thibault Debatty
 *
 */
@Mojo(name = "publish")
public class PublishMojo extends AbstractSparkPackageMojo {

    private static final String[] LICENCES = new String[]{
        "Apache-2.0", "BSD 3-Clause", "BSD 2-Clause", "GPL-2.0", "GPL-3.0",
        "LGPL-2.1", "LGPL-3.0", "MIT", "MPL-2.0", "EPL-1.0"};


    @Override
    public final void realexe() throws MojoFailureException {

        File zip_file = new File(getZipPath());
        byte[] zip_base64 = null;
        try {
            zip_base64 = Base64.encodeBase64(
                    FileUtils.readFileToByteArray(zip_file));

        } catch (IOException ex) {
            throw new MojoFailureException("Error!",  ex);
        }

        HttpEntity request = MultipartEntityBuilder.create()
                .addBinaryBody(
                        "artifact_zip",
                        zip_base64,
                        ContentType.APPLICATION_OCTET_STREAM,
                        "artifact_zip")
                .addTextBody("version", getVersion())
                .addTextBody("license_id", getLicenseId())
                .addTextBody("git_commit_sha1", getGitCommit())
                .addTextBody("name", getOrganization() + "/" + getRepo())
                .build();

        HttpPost post = new HttpPost(getSparkpackagesUrl());
        post.setEntity(request);

        post.setHeader(
                "Authorization",
                getAuthorizationHeader());

        getLog().info("Executing request " + post.getRequestLine());

        // .setProxy(new HttpHost("127.0.0.1", 8888))
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (IOException ex) {
            throw new MojoFailureException(
                    "Failed to perform HTTP request", ex);
        }
        getLog().info("Server responded " + response.getStatusLine());

        HttpEntity response_content = response.getEntity();
        if (response_content == null) {
            throw new MojoFailureException(
                    "Server responded with an empty response");
        }

        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br;
        try {
            br = new BufferedReader(
                    new InputStreamReader(response_content.getContent()));

            while ((line = br.readLine()) != null) {
                    sb.append(line);
            }
        } catch (IOException | UnsupportedOperationException ex) {
            throw new MojoFailureException("Could not read response...", ex);
        }
        System.out.println(sb.toString());

        try {
            System.out.println(EntityUtils.toString(response_content));
        } catch (IOException | ParseException ex) {

        }
    }

    final String getGitCommit() throws MojoFailureException {
              Repository git_repository = null;
        try {
            git_repository = new FileRepositoryBuilder()
                    .setMustExist(true)
                    .findGitDir(
                            new File(
                                    getProject().getBasedir().getAbsolutePath()))
                    .build();
        } catch (IOException ex) {
            throw new MojoFailureException("GIT repo not found!", ex);
        }


        ObjectId resolve = null;
        try {
            resolve = git_repository.resolve("HEAD");
        } catch (IncorrectObjectTypeException ex) {
            throw new MojoFailureException("GIT error!", ex);
        } catch (RevisionSyntaxException | IOException ex) {
            throw new MojoFailureException("GIT error!", ex);
        }

        return resolve.getName();
    }

    final String getLicenseId() throws MojoFailureException {
        for (Object license_object : getProject().getLicenses()) {
            License license = (License) license_object;
            String license_name = license.getName();

            for (int i = 0; i < LICENCES.length; i++) {
                if (license_name.equalsIgnoreCase(LICENCES[i])) {
                    return String.valueOf(i);
                }
            }
        }

        throw new MojoFailureException("Could not find a supported licence");
    }

    final String getAuthorizationHeader() throws MojoFailureException {
        if (getUsername() == null || getToken() == null
                || getUsername().isEmpty() || getToken().isEmpty()) {
            throw new MojoFailureException(
                    "Username and/or token are not defined");
        }

        return "Basic "
                + Base64.encodeBase64String(
                        (getUsername() + ":" + getToken()).getBytes());
    }
}
