/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.graphiql;

import edu.umd.cs.findbugs.annotations.NonNull;

import io.jooby.Extension;
import io.jooby.Jooby;
import io.jooby.MediaType;

/**
 * GraphiQL module: https://github.com/graphql/graphiql.
 *
 * Usage:
 *
 * <pre>{@code
 *
 *   install(new GraphiQLModule());
 *
 * }</pre>
 *
 * Module install a GET route under <code>/graphql</code> path. Optionally, you can change
 * the route path by setting the <code>graphql.path</code> property in your application
 * configuration file.
 *
 * @author edgar
 * @since 2.4.0
 */
public class GraphiQLModule implements Extension {

  private static final String RESOURCES_CSS =
      "  <link href=\"{{contextPath}}/graphql/static/graphiql.css\" rel=\"stylesheet\" />\n";

  private static final String RESOURCES_JS = "  <script src=\"{{contextPath}}/graphql/static/graphiql.min.js\"></script>\n";

  @Override public void install(@NonNull Jooby application) throws Exception {
    String cpath = application.getContextPath();
    if (cpath == null || cpath.equals("/")) {
      cpath = "";
    }
    String index = INDEX.replace("{{contextPath}}", cpath);

    String graphqlPath = application.getEnvironment().getProperty("graphql.path", "/graphql");
    application.assets("/graphql/static/*", "/graphiql");
    application.get(graphqlPath, ctx -> ctx.setResponseType(MediaType.html).send(index));
  }

  private static final String INDEX = "\n"
      + "<!DOCTYPE html>\n"
      + "<html lang=\"en\">\n"
      + "<head>\n"
      + "  <meta charset=\"utf-8\" />\n"
      + "  <meta name=\"robots\" content=\"noindex\" />\n"
      + "  <meta name=\"referrer\" content=\"origin\" />\n"
      + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n"
      + "  <title>SWAPI GraphQL API</title>\n"
      + "  <style>\n"
      + "    body {\n"
      + "      height: 100vh;\n"
      + "      margin: 0;\n"
      + "      overflow: hidden;\n"
      + "    }\n"
      + "    #splash {\n"
      + "      color: #333;\n"
      + "      display: flex;\n"
      + "      flex-direction: column;\n"
      + "      font-family: system, -apple-system, \"San Francisco\", \".SFNSDisplay-Regular\", \"Segoe UI\", Segoe, \"Segoe WP\", \"Helvetica Neue\", helvetica, \"Lucida Grande\", arial, sans-serif;\n"
      + "      height: 100vh;\n"
      + "      justify-content: center;\n"
      + "      text-align: center;\n"
      + "    }\n"
      + "  </style>\n"
      + "  <link rel=\"icon\" href=\"favicon.ico\">\n"
      + RESOURCES_CSS
      + "</head>\n"
      + "<body>\n"
      + "  <div id=\"splash\">\n"
      + "    Loading&hellip;\n"
      + "  </div>\n"
      + "  <script src=\"//cdn.jsdelivr.net/es6-promise/4.0.5/es6-promise.auto.min.js\"></script>\n"
      + "  <script src=\"https://cdn.jsdelivr.net/npm/react/umd/react.production.min.js\"></script>\n"
      + "  <script src=\"https://cdn.jsdelivr.net/npm/react-dom/umd/react-dom.production.min.js\"></script>\n"
      + RESOURCES_JS
      + "  <script>\n"
      + "      // Parse the search string to get url parameters.\n"
      + "      var search = window.location.search;\n"
      + "      var parameters = {};\n"
      + "      search.substr(1).split('&').forEach(function (entry) {\n"
      + "        var eq = entry.indexOf('=');\n"
      + "        if (eq >= 0) {\n"
      + "          parameters[decodeURIComponent(entry.slice(0, eq))] =\n"
      + "            decodeURIComponent(entry.slice(eq + 1));\n"
      + "        }\n"
      + "      });\n"
      + "\n"
      + "      // if variables was provided, try to format it.\n"
      + "      if (parameters.variables) {\n"
      + "        try {\n"
      + "          parameters.variables =\n"
      + "            JSON.stringify(JSON.parse(parameters.variables), null, 2);\n"
      + "        } catch (e) {\n"
      + "          // Do nothing, we want to display the invalid JSON as a string, rather\n"
      + "          // than present an error.\n"
      + "        }\n"
      + "      }\n"
      + "\n"
      + "      // When the query and variables string is edited, update the URL bar so\n"
      + "      // that it can be easily shared\n"
      + "      function onEditQuery(newQuery) {\n"
      + "        parameters.query = newQuery;\n"
      + "        updateURL();\n"
      + "      }\n"
      + "      function onEditVariables(newVariables) {\n"
      + "        parameters.variables = newVariables;\n"
      + "        updateURL();\n"
      + "      }\n"
      + "      function onEditOperationName(newOperationName) {\n"
      + "        parameters.operationName = newOperationName;\n"
      + "        updateURL();\n"
      + "      }\n"
      + "      function updateURL() {\n"
      + "        var newSearch = '?' + Object.keys(parameters).filter(function (key) {\n"
      + "          return Boolean(parameters[key]);\n"
      + "        }).map(function (key) {\n"
      + "          return encodeURIComponent(key) + '=' +\n"
      + "            encodeURIComponent(parameters[key]);\n"
      + "        }).join('&');\n"
      + "        history.replaceState(null, null, newSearch);\n"
      + "      }\n"
      + "\n"
      + "       function graphQLFetcher(graphQLParams) {\n"
      + "          // This example expects a GraphQL server at the path /graphql.\n"
      + "          // Change this to point wherever you host your GraphQL server.\n"
      + "          return fetch(parameters.fetchURL || 'https://swapi-graphql.netlify.app/.netlify/functions/index', {\n"
      + "            method: 'post',\n"
      + "            headers: {\n"
      + "              'Accept': 'application/json',\n"
      + "              'Content-Type': 'application/json'\n"
      + "            },\n"
      + "            body: JSON.stringify(graphQLParams),\n"
      + "          }).then(function (response) {\n"
      + "            return response.text();\n"
      + "          }).then(function (responseBody) {\n"
      + "            try {\n"
      + "              return JSON.parse(responseBody);\n"
      + "            } catch (error) {\n"
      + "              return responseBody;\n"
      + "            }\n"
      + "          });\n"
      + "        }\n"
      + "\n"
      + "      // Render <GraphiQL /> into the body.\n"
      + "      ReactDOM.render(\n"
      + "        React.createElement(GraphiQL, {\n"
      + "          fetcher: graphQLFetcher,\n"
      + "          query: parameters.query,\n"
      + "          variables: parameters.variables,\n"
      + "          operationName: parameters.operationName,\n"
      + "          onEditQuery: onEditQuery,\n"
      + "          onEditVariables: onEditVariables,\n"
      + "          onEditOperationName: onEditOperationName\n"
      + "        }),\n"
      + "        document.body,\n"
      + "      );\n"
      + "  </script>\n"
      + "</body>\n"
      + "</html>\n"
      + "\n\n"
      + "<!DOCTYPE html>\n"
      + "<html lang=\"en\">\n"
      + "<head>\n"
      + "  <meta charset=\"utf-8\" />\n"
      + "  <meta name=\"robots\" content=\"noindex\" />\n"
      + "  <meta name=\"referrer\" content=\"origin\" />\n"
      + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n"
      + "  <title>SWAPI GraphQL API</title>\n"
      + "  <style>\n"
      + "    body {\n"
      + "      height: 100vh;\n"
      + "      margin: 0;\n"
      + "      overflow: hidden;\n"
      + "    }\n"
      + "    #splash {\n"
      + "      color: #333;\n"
      + "      display: flex;\n"
      + "      flex-direction: column;\n"
      + "      font-family: system, -apple-system, \"San Francisco\", \".SFNSDisplay-Regular\", \"Segoe UI\", Segoe, \"Segoe WP\", \"Helvetica Neue\", helvetica, \"Lucida Grande\", arial, sans-serif;\n"
      + "      height: 100vh;\n"
      + "      justify-content: center;\n"
      + "      text-align: center;\n"
      + "    }\n"
      + "  </style>\n"
      + "  <link rel=\"icon\" href=\"favicon.ico\">\n"
      + "  <link type=\"text/css\" href=\"//unpkg.com/graphiql/graphiql.min.css\" rel=\"stylesheet\" />\n"
      + "</head>\n"
      + "<body>\n"
      + "  <div id=\"splash\">\n"
      + "    Loading&hellip;\n"
      + "  </div>\n"
      + "  <script src=\"//cdn.jsdelivr.net/es6-promise/4.0.5/es6-promise.auto.min.js\"></script>\n"
      + "  <script src=\"https://cdn.jsdelivr.net/npm/react/umd/react.production.min.js\"></script>\n"
      + "  <script src=\"https://cdn.jsdelivr.net/npm/react-dom/umd/react-dom.production.min.js\"></script>\n"
      + "  <script src=\"//unpkg.com/graphiql/graphiql.min.js\"></script>\n"
      + "  <script>\n"
      + "      // Parse the search string to get url parameters.\n"
      + "      var search = window.location.search;\n"
      + "      var parameters = {};\n"
      + "      search.substr(1).split('&').forEach(function (entry) {\n"
      + "        var eq = entry.indexOf('=');\n"
      + "        if (eq >= 0) {\n"
      + "          parameters[decodeURIComponent(entry.slice(0, eq))] =\n"
      + "            decodeURIComponent(entry.slice(eq + 1));\n"
      + "        }\n"
      + "      });\n"
      + "\n"
      + "      // if variables was provided, try to format it.\n"
      + "      if (parameters.variables) {\n"
      + "        try {\n"
      + "          parameters.variables =\n"
      + "            JSON.stringify(JSON.parse(parameters.variables), null, 2);\n"
      + "        } catch (e) {\n"
      + "          // Do nothing, we want to display the invalid JSON as a string, rather\n"
      + "          // than present an error.\n"
      + "        }\n"
      + "      }\n"
      + "\n"
      + "      // When the query and variables string is edited, update the URL bar so\n"
      + "      // that it can be easily shared\n"
      + "      function onEditQuery(newQuery) {\n"
      + "        parameters.query = newQuery;\n"
      + "        updateURL();\n"
      + "      }\n"
      + "      function onEditVariables(newVariables) {\n"
      + "        parameters.variables = newVariables;\n"
      + "        updateURL();\n"
      + "      }\n"
      + "      function onEditOperationName(newOperationName) {\n"
      + "        parameters.operationName = newOperationName;\n"
      + "        updateURL();\n"
      + "      }\n"
      + "      function updateURL() {\n"
      + "        var newSearch = '?' + Object.keys(parameters).filter(function (key) {\n"
      + "          return Boolean(parameters[key]);\n"
      + "        }).map(function (key) {\n"
      + "          return encodeURIComponent(key) + '=' +\n"
      + "            encodeURIComponent(parameters[key]);\n"
      + "        }).join('&');\n"
      + "        history.replaceState(null, null, newSearch);\n"
      + "      }\n"
      + "\n"
      + "       function graphQLFetcher(graphQLParams) {\n"
      + "          // This example expects a GraphQL server at the path /graphql.\n"
      + "          // Change this to point wherever you host your GraphQL server.\n"
      + "          return fetch(parameters.fetchURL || 'https://swapi-graphql.netlify.app/.netlify/functions/index', {\n"
      + "            method: 'post',\n"
      + "            headers: {\n"
      + "              'Accept': 'application/json',\n"
      + "              'Content-Type': 'application/json'\n"
      + "            },\n"
      + "            body: JSON.stringify(graphQLParams),\n"
      + "          }).then(function (response) {\n"
      + "            return response.text();\n"
      + "          }).then(function (responseBody) {\n"
      + "            try {\n"
      + "              return JSON.parse(responseBody);\n"
      + "            } catch (error) {\n"
      + "              return responseBody;\n"
      + "            }\n"
      + "          });\n"
      + "        }\n"
      + "\n"
      + "      // Render <GraphiQL /> into the body.\n"
      + "      ReactDOM.render(\n"
      + "        React.createElement(GraphiQL, {\n"
      + "          fetcher: graphQLFetcher,\n"
      + "          query: parameters.query,\n"
      + "          variables: parameters.variables,\n"
      + "          operationName: parameters.operationName,\n"
      + "          onEditQuery: onEditQuery,\n"
      + "          onEditVariables: onEditVariables,\n"
      + "          onEditOperationName: onEditOperationName\n"
      + "        }),\n"
      + "        document.body,\n"
      + "      );\n"
      + "  </script>\n"
      + "</body>\n"
      + "</html>\n"
      + "\n";
}
