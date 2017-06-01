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

import junit.framework.TestCase;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

/**
 *
 * @author Thibault Debatty
 */
public class ZipMojoIT extends TestCase {

    /**
     * Test of execute method, of class ZipMojo.
     *
     * @throws org.apache.maven.it.VerificationException if ??
     */
    public final void testExecute() throws VerificationException {

        System.out.println(System.getProperty("buildDirectory"));
        System.out.println(System.getProperty("version"));

        Verifier verifier = new Verifier(
                getClass().getClassLoader().getResource("it-001").getPath());


        MavenXpp3Reader pom_reader = new MavenXpp3Reader();
        //Model model = pomReader.read(ReaderFactory.newXmlReader(new File(
        //)));

        verifier.executeGoal("sparkpackage:zip");
        verifier.assertFilePresent("target/bar-0.1-SNAPSHOT.zip");
    }
}
