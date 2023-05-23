/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package i2598

import io.jooby.kt.Kooby

class App2598 :
  Kooby({
    get("/2598") {
      val sign = mutableListOf<Int>()
      ctx.send("{\"success\":\"true\"}")
      // some imaginary long running operation here
      sign.removeIf { it == 1 }
      return@get ctx
    }
  })
