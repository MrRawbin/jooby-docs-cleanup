/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package issues.i1905;

import io.jooby.Jooby;

public class App1905 extends Jooby {
  {
    install(SubApp1905::new);

    install("/inline", SubApp1905::new);

    install(
        "/lambda-body",
        () -> {
          SubApp1905 app = new SubApp1905();
          doSomething(app);
          return app;
        });

    install("/instance-reference", this::instanceMethod);

    install("/static-reference", App1905::staticMethod);
  }

  private static SubApp1905 staticMethod() {
    SubApp1905 app = new SubApp1905();
    return app;
  }

  private SubApp1905 instanceMethod() {
    SubApp1905 app = new SubApp1905();
    return app;
  }

  private void doSomething(SubApp1905 app) {}
}
