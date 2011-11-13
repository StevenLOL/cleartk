/** 
 * Copyright (c) 2011, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package org.cleartk.timeml;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <br>
 * Copyright (c) 2011, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * @author Steven Bethard
 */
public class TimeMLAnnotateTest extends TimeMLTestBase{
  private File tempDir;

  private File inputFile;

  private File outputFile;

  @Before
  public void setUp() throws Exception {
    this.tempDir = File.createTempFile("TimeMLAnnotateTest", "");
    this.tempDir.delete();
    this.tempDir.mkdir();
    this.inputFile = new File(this.tempDir, "input.txt");
    FileUtils.writeStringToFile(
        this.inputFile,
        "In August 2010, the U.S. Central Intelligence Agency had identified a compound in Abbottabad in Pakistan as the likely location of bin Laden. On May 1, 2011, President Barack Obama ordered Navy SEALs to assault the compound.");
    this.outputFile = new File(this.tempDir, "input.txt.tml");
  }

  @After
  public void tearDown() throws Exception {
    FileUtils.deleteDirectory(this.tempDir);
  }

  @Test
  public void test() throws Exception {
    assumeBigMemoryTestsEnabled();
    this.logger.info(BIG_MEMORY_TEST_MESSAGE);

    TimeMLAnnotate.main(this.inputFile.getPath(), this.tempDir.getPath());
    String output = FileUtils.readFileToString(this.outputFile);
    output = output.replaceAll("\r\n", "\n");
    // @formatter:off
    String expected =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<TimeML>" +
      "In" +
      " <TIMEX3 tid=\"t1\" type=\"DATE\">August 2010</TIMEX3>, the U.S. Central Intelligence Agency had" +
      " <EVENT eid=\"e1\" class=\"OCCURRENCE\" tense=\"PAST\" aspect=\"PERFECTIVE\" polarity=\"POS\" modality=\"NONE\">identified</EVENT>" +
      " a compound in Abbottabad in Pakistan as the likely location of bin Laden. " +
      "On <TIMEX3 tid=\"t2\" type=\"DATE\">May 1, 2011</TIMEX3>, President Barack Obama" +
      " <EVENT eid=\"e2\" class=\"I_ACTION\" tense=\"PAST\" aspect=\"NONE\" polarity=\"POS\" modality=\"NONE\">ordered</EVENT>" +
      " Navy SEALs to" +
      " <EVENT eid=\"e3\" class=\"OCCURRENCE\" tense=\"INFINITIVE\" aspect=\"NONE\" polarity=\"POS\" modality=\"NONE\">assault</EVENT>" +
      " the compound." +
      // this TLINK should really be OVERLAP - feel free to fix it if model performance improves
      "<TLINK relType=\"AFTER\" eventID=\"e1\" relatedToTime=\"t1\"/>" +
      "<TLINK relType=\"OVERLAP\" eventID=\"e3\" relatedToTime=\"t2\"/>" +
      "<TLINK relType=\"BEFORE\" eventID=\"e2\" relatedToEvent=\"e3\"/>" +
      "<TLINK relType=\"BEFORE\" eventID=\"e1\" relatedToEvent=\"e2\"/>" +
      "<TLINK relType=\"OVERLAP\" eventID=\"e2\" relatedToTime=\"t2\"/>" +
      "</TimeML>";
    // @formatter:on
    Assert.assertEquals("TimeML output should match", expected, output);
  }
}
