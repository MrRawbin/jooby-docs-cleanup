/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package output;

import org.junit.jupiter.api.Test;

public class ASMPrinter {

  @Test
  public void mvcExtension() throws Exception {
    //     ASMifier.main(new String[]{MvcExtension.class.getName()});
  }

  @Test
  public void mvcDispatch() throws Exception {
    // ASMifier.main(new String[]{MvcDispatch.class.getName()});
  }

  @Test
  public void myController() throws Exception {
    //     ASMifier.main(new String[]{MyControllerHandler.class.getName()});
  }

  @Test
  public void nullRoutes() throws Exception {
    // ASMifier.main(new String[]{"source.SuspendRoute"});
  }
}
