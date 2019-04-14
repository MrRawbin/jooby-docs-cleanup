/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    Copyright 2014 Edgar Espina
 */
package io.jooby.internal;

import io.jooby.Context;
import io.jooby.Renderer;
import io.jooby.Route;
import io.jooby.Router;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class RouterMatch implements Router.Match {

  boolean matches;

  private Route route;

  private Map vars = Collections.EMPTY_MAP;

  private Route.Handler handler;

  public void key(List<String> keys) {
    for (int i = 0; i < keys.size(); i++) {
      vars.put(keys.get(i), vars.remove(i));
    }
  }

  public void value(String value) {
    if (vars == Collections.EMPTY_MAP) {
      vars = new LinkedHashMap();
    }
    vars.put(vars.size(), value);
  }

  public void pop() {
    vars.remove(vars.size() - 1);
  }

  public void methodNotAllowed() {
    handler = Route.METHOD_NOT_ALLOWED;
  }

  @Override public boolean matches() {
    return matches;
  }

  @Override public Route route() {
    return route;
  }

  @Override public Map<String, String> pathMap() {
    return vars;
  }

  public RouterMatch found(Route route) {
    this.route = route;
    this.matches = true;
    return this;
  }

  public void execute(Context context) {
    context.setPathMap(vars);
    context.setRoute(route);
    try {
      route.getPipeline().apply(context);
    } catch (Throwable x) {
      context.sendError(x);
    }
  }

  public RouterMatch missing(String method, String path, Renderer renderer) {
    Route.Handler h;
    if (this.handler == null) {
      h = path.endsWith("/favicon.ico") ? Route.FAVICON : Route.NOT_FOUND;
    } else {
      h = this.handler;
    }
    this.route = new Route(method, path, emptyList(), String.class, h, null, null, renderer, emptyMap());
    return this;
  }
}
