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

/**
 *
 * @author tibo
 */
public class ZipMojoTest extends MojoTestCase {

    /**
     * Test of execute method, of class ZipMojo.
     *
     * @throws java.lang.Exception if the test failed
     */
    public final void testParseOranizationAndRepo() throws Exception {
        System.out.println("parse organization and repo");
        File pom = getTestFile("src/test/resources/zipmojo/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        ZipMojo mojo = (ZipMojo) lookupConfiguredMojo(pom, "zip");
        assertNotNull(mojo);

        mojo.parseOranizationAndRepo();
        assertEquals("foo", mojo.getOrganization());

        mojo.parseVersion();
        assertEquals("0.1-SNAPSHOT", mojo.getVersion());
    }
}
