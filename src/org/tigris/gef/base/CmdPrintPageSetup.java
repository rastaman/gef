/*
 * CmdPrintPageSetup.java
 */
package org.tigris.gef.base;

import java.util.*;
import java.awt.*;
import java.io.*;

import java.awt.print.*;

import org.tigris.gef.presentation.Fig;

/**
 * Cmd to setup a page for printing.
 *
 * Only works under JDK 1.2 and above. 
 *
 * @author Eugenio Alvarez
 *
 * @see CmdPrint
 */
public class CmdPrintPageSetup extends Cmd {

  CmdPrint cmdPrint;

  public CmdPrintPageSetup(CmdPrint cmdPrint) { 
      super("Page Setup..."); 
      this.cmdPrint = cmdPrint;
  }

  public void doIt() {
      cmdPrint.doPageSetup();
  }

  public void undoIt() {
    System.out.println("Undo does not make sense for CmdPrintPageSetup");
  }

} /* end class CmdPrintPageSetup */

