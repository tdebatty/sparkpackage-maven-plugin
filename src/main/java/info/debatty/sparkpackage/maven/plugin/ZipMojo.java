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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author Thibault Debatty
 * @goal zip
 * 
 */

public class ZipMojo extends AbstractSparkPackageMojo {

    /**
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException"
     */
    public void _execute() throws MojoExecutionException, MojoFailureException {
        
        FileOutputStream dest = null;
        try {
            int BUFFER = 4096;
            dest = new FileOutputStream(zip_path);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];
            
            FileInputStream fi = new FileInputStream(new File(jar_path));
            BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
            
            ZipEntry entry = new ZipEntry(repo + "-" + version + ".jar");
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            
            MavenProject modified_project = (MavenProject) project.clone();
            modified_project.setArtifactId(repo);
            modified_project.setGroupId(organization);
            
            entry = new ZipEntry(repo + "-" + version + ".pom");
            out.putNextEntry(entry);
            
            modified_project.writeModel(new OutputStreamWriter(out));
            
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ZipMojo.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ZipMojo.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            try {
                dest.close();
            } catch (IOException ex) {
                Logger.getLogger(ZipMojo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
