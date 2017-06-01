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
import java.io.IOException;
import java.util.HashMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
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

            /*
        'Apache-2.0' => 0,
    'BSD 3-Clause' => 1,
    'BSD 2-Clause' => 2,
    'GPL-2.0' => 3,
    'GPL-3.0' => 4,
    'LGPL-2.1' => 6,
    'LGPL-3.0' => 7,
    'MIT' => 8,
    'MPL-2.0' => 9,
    'EPL-1.0' => 10
        */
    private static final HashMap<String, Integer> LICENCES =
            new HashMap<String, Integer>();


    @Override
    public final void realexe() throws MojoFailureException {

        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://localhost/tests/spark-server.php");

        File zip_file = new File(this.zip_path);

        byte[] zip_base64 = null;
        try {
            zip_base64 = Base64.encodeBase64(
                    FileUtils.readFileToByteArray(zip_file));

        } catch (IOException ex) {
            throw new MojoFailureException("Error!",  ex);
        }

        Repository git_repository = null;
        try {
            git_repository = new FileRepositoryBuilder()
                    .setGitDir(new File(getProject().getBasedir().getAbsolutePath()))
                    .build();
        } catch (IOException ex) {
            throw new MojoFailureException("GIT repo not found!", ex);
        }


        ObjectId resolve = null;
        try {
            resolve = git_repository.resolve("HEAD");
        } catch (IncorrectObjectTypeException ex) {
            throw new MojoFailureException("GIT error!", ex);
        } catch (RevisionSyntaxException ex) {
            throw new MojoFailureException("GIT error!", ex);
        } catch (IOException ex) {
            throw new MojoFailureException("GIT error!", ex);
        }

        //System.out.println(resolve.toString());
        for (Object license_object : getProject().getLicenses()) {
            License license = (License) license_object;
            System.out.println(license.getName());
        }

        // resolve.getName();

        HttpEntity request = MultipartEntityBuilder.create()
                .addBinaryBody(
                        "artifact_zip",
                        zip_base64,
                        ContentType.APPLICATION_OCTET_STREAM,
                        "artifact_zip")
                .addTextBody("version", this.version)
                .addTextBody("license_id", "7")
                .addTextBody("git_commit_sha1", "123")
                .addTextBody("name", this.organization + "/" + this.repo)
                .build();

        post.setEntity(request);

        String username = "tdebatty";
        String token = "58b7ae352cd3046b31e1e53182f1292c7575a88c";

        post.setHeader(
                "Authorization",
                "Basic: "
                        + Base64.encodeBase64String(
                                (username + ":" + token).getBytes()));

        System.out.println("executing request " + post.getRequestLine());

        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (IOException ex) {
            throw new MojoFailureException(
                    "Failed to perform HTTP request", ex);
        }
        System.out.println(response.getStatusLine());

        HttpEntity response_content = response.getEntity();


        if (response_content == null) {
            throw new MojoFailureException(
                    "Server responded with an empty response");
        }

        try {
            System.out.println(EntityUtils.toString(response_content));
        } catch (IOException ex) {

        } catch (ParseException ex) {

        }
    }
}
