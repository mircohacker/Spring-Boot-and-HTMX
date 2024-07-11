Spring Boot and HTMX: The boring app
====

## Motivation

Most apps I touched in the wild follow the same two tiered approach.
A backend delivering JSON (some may call this REST) and a frontend framework, consuming JSON from the backend converting
it to the HTML displayed to the user.
Worst case, one team manages multiple frontends or backends at once.

For most business apps this setup is already more complex than
necessary. With this post I want to propose a simpler approach to the
typical business app, centered around a Spring Boot backend. No worries
I won’t torture you with JSF or Primefaces.

The goal is to reduce the [accidental complexity](https://en.wikipedia.org/wiki/No_Silver_Bullet) in the implemented
system.

With [Stackoverflow](https://stackoverflow.blog/2020/11/23/the-macro-problem-with-microservices/)
and [Shopify](https://shopify.engineering/shopify-monolith) there are
examples in the wild of hugely successful enterprises with monolithic
core applications. Of course at their scale this also has downsides and
both are currently investigating how to make their codebase more
modular. But if these two managed to grow to this scale with a monolith
it seems not unlikely that this architecture, with adequate discipline, is
sufficient for a lot of apps.

The two key elements I will use to make developing this application a
pleasant and simple experience are:

1. [Apache Freemarker](https://freemarker.apache.org/) as templating engine

2. [HTMX](https://htmx.org/) in order to make the frontend more responsive

I will take you on the journey which lead to this application. I will
describe the compromises and decisions made. If you are in a hurry or
impatient you can simply check out the accompanying [Git
Repo](https://github.com/mircohacker/Spring-Boot-and-HTMX) and follow the README to build and deploy the
application to AWS Lambda.

## Templating engines

First things first. We want to render HTML on the server painlessly, so
we need a so called templating engine which lets us define a certain
layout with placeholders and provides a mechanism to fill these
placeholders with real values.

I investigated different templating engines [Mustache](https://mustache.github.io/),
[Handlebars.java](https://handlebarsjs.com/), [Thymeleaf](https://www.thymeleaf.org/) and [Apache
Freemarker](https://freemarker.apache.org/).

The requirements where:

- Simplicity: I know this is highly subjective. The whole reason for
  the app and the blogpost is to avoid accidental complexity wherever
  possible and reserve the complex solutions for the complex problems.

- Component reusability: In most apps there will be at least a few
  (possibly many) recurring elements to be used at various locations
  throughout the app. Think about buttons.
  As we want to avoid duplication, we need a way to define these components at one place and use them wherever we need.

### Mustache

I started with Mustache because it promises simplicity.
There is a Spring Boot starter, which means it is easy to include.
Code reuse in Mustache is implemented using so-called [partials](https://mustache.github.io/mustache.5.html).
Partials in Mustache sadly cannot receive parameters (e.g. Button text).
Therefor Mustache cannot be used to create atomic reusable components.

### Handlebars.java

The next natural stop is Handlebars.java.
It extends the Mustache syntax and offers a way to parametrise partials.
This can be used to build components.
With syntax like `{{> content}}` inside the component you can create a named slot where other things could insert.
A full example would look like this:

`site.hbs` will include the `layout.hbs` file and replace certain parameters within the layout partial.

layout.hbs

```handlebars
<div>
    <div class="nav">
        {{> nav}}
    </div>
    <div class="content">
        {{> content}}
    </div>
    {{#> footer }}
        Footer default
    {{/footer}}
</div>
```

site.hbs

```handlebars
{{#> layout}}
    {{#*inline "nav"}}
        My Nav
    {{/inline}}
    {{#*inline "content"}}
        My Content
    {{/inline}}
{{/layout}}

```

Together it would render to:

```htmlbars
<div>
    <div class="nav">
        My Nav
    </div>
    <div class="content">
        My Content
    </div>
    Footer default
</div>
```

To play around with it yourself you can have a look at
the [playground](https://handlebarsjs.com/playground.html#format=1&currentExample=%7B%22template%22%3A%22%7B%7B%23%3E%20layout%7D%7D%5Cn%20%20%7B%7B%23*inline%20%5C%22nav%5C%22%7D%7D%5Cn%20%20%20%20My%20Nav%5Cn%20%20%7B%7B%2Finline%7D%7D%5Cn%20%20%7B%7B%23*inline%20%5C%22content%5C%22%7D%7D%5Cn%20%20%20%20My%20Content%5Cn%20%20%7B%7B%2Finline%7D%7D%5Cn%7B%7B%2Flayout%7D%7D%22%2C%22partials%22%3A%5B%7B%22name%22%3A%22layout%22%2C%22content%22%3A%22%3Cdiv%3E%5Cn%20%20%3Cdiv%20class%3D%5C%22nav%5C%22%3E%5Cn%20%20%20%20%7B%7B%3E%20nav%7D%7D%5Cn%20%20%3C%2Fdiv%3E%5Cn%20%20%3Cdiv%20class%3D%5C%22content%5C%22%3E%5Cn%20%20%20%20%7B%7B%3E%20content%7D%7D%5Cn%20%20%3C%2Fdiv%3E%5Cn%20%20%7B%7B%23%3E%20footer%20%7D%7D%5Cn%20%20Footer%20default%5Cn%20%20%7B%7B%2Ffooter%7D%7D%5Cn%3C%2Fdiv%3E%22%7D%5D%2C%22input%22%3A%22null%5Cn%22%2C%22output%22%3A%22%3Cdiv%3E%5Cn%20%20%3Cdiv%20class%3D%5C%22nav%5C%22%3E%5Cn%20%20%20%20%20%20%20%20My%20Nav%5Cn%20%20%3C%2Fdiv%3E%5Cn%20%20%3Cdiv%20class%3D%5C%22content%5C%22%3E%5Cn%20%20%20%20%20%20%20%20My%20Content%5Cn%20%20%3C%2Fdiv%3E%5Cn%20%20Footer%20default%5Cn%3C%2Fdiv%3E%22%2C%22preparationScript%22%3A%22%5Cn%22%2C%22handlebarsVersion%22%3A%224.7.8%22%7D)

The syntax with curly braces, hashtags and greater than does not feel super intuitive to me,
so I searched further.
But if you feel different, be my guest and use what suits you.

### Thymeleaf

After this I had a look at the "Industry Standard" Thymeleaf.
The mixture of templating directives and html attributes is not appealing to me.
I wanted to replace a complex web framework with a simple templating engine.
Thymeleaf does not seem simple to me 🤷.
For the record this is what Thymeleaf looks like:

```thymeleafexpressions
<table>
  <thead>
    <tr>
      <th th:text="#{msgs.headers.name}">Name</th>
      <th th:text="#{msgs.headers.price}">Price</th>
    </tr>
  </thead>
  <tbody>
    <tr th:each="prod: ${allProducts}">
      <td th:text="${prod.name}">Oranges</td>
      <td th:text="${#numbers.formatDecimal(prod.price, 1, 2)}">0.99</td>
    </tr>
  </tbody>
</table>
```

### Freemarker

The last templating Engine proposed by the excellent [Spring Initializer](https://start.spring.io/) was Freemarker.
It is elegant, clearly distinguishes between templating language and template contents.
With [macros](https://freemarker.apache.org/docs/ref_directive_macro.html) there is also the possibility to create
reusable components.
And getting started is simple as well.
Just include the `spring-boot-starter-freemarker` dependency.
The same layout from handlebars above looks like this in freemarker:

```injectedfreemarker
<#macro layout nav content footer="Footer default">
    <div>
        <div class="nav">
            ${nav}
        </div>
        <div class="content">
            ${content}
        </div>
        ${footer}
    </div>
</#macro>

<@layout nav="My Nav" content="My content"></@layout>
```

I like this engine more than handlebars because it is more versatile
(multiple macros per file) and is also cleaner to read.

## HTML to the browser

So now that we know how we want to generate our HTML let's have a look how we bring this HTML in the browser of a user.
We create a new project using the Spring initializer containing the following packages:

- spring-web
- spring-freemarker
- dev-tools (for hot reloading)

Next we create the following `index.tlfh` file in `src/main/resources/templates`:

```html
<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>SSR Spring boot demo</title>
</head>

<body>
<div>
    <h1> Hello from Freemarker</h1>
</div>
</body>
```

Run `./gradlew booRun` and got to http://localhost:8080 to see our beautiful first rendered page.

Note: The default file extension for freemarker is `.ftl`.
Spring uses the sensible extensions `.ftlh` which tells freemarker to escape HTML passed via a model.

Now to make is less bland I (as a backend developer) add bootstrap 😉.
It is up to you to choose tailwind, bulma, materialUI or something completely different.
Because I do want to have this whole app as simple to maintain as possible,
I add bootstrap as webjar.
This way the bootstrap dependency version is discoverable via the `build.gradle` file and by extensions also by tools
like renovate or dependabot.
This leads to easier version updates.
The alternative would be to have tags like this

```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
```

with fixed versions somewhere deep inside some template file and I don't want this.

To include the webjars we need to add dependencies `org.webjars:bootstrap:5.3.3` and `org.webjars:webjars-locator:0.52`
to our `build.gradle`.
The first one contains the build artifacts of this version.
The `webjar-locator` is responsible to resolve the path `/webjars/bootstrap/css/bootstrap.min.css` to the actual file
stored in the dependency.
Our "styled" index looks like this

```html
<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>SSR Spring boot demo</title>

    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <script src="/webjars/bootstrap/js/bootstrap.bundle.min.js"></script>
</head>

<body>
<div>
    <h1> Hello from Freemarker</h1>
</div>
</body>
```

Let's add some "real" functionality to this App. We want to have a product overview and a product detail page.
We implement a controller and two fragments for the overview and the product detail page.

```kotlin
@Controller
class ProductsController {

    val products = listOf(Product(id = "one"), Product())

    @GetMapping("/products")
    fun getProducts(): ModelAndView {
        return ModelAndView("sites/products", mapOf("products" to products))
    }

    @GetMapping("/products/{id}")
    fun getProduct(@PathVariable id: String): ModelAndView {
        return ModelAndView("sites/product", mapOf("product" to getProductById(id)))
    }
}
```

First we define a custom [macro](https://freemarker.apache.org/docs/ref_directive_macro.html) named `page`
with `<#macro page>`.
This macro contains the repeating HTML boilerplate.
The macro is located in the file `fragments/shared-components.ftlh`.
The `<#nested/>` directive will expand to whatever is contained in the macro on execution.

```injectedfreemarker
<#macro page>
<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>SSR Spring boot demo</title>

    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <script src="/webjars/bootstrap/js/bootstrap.bundle.min.js"></script>
</head>

<body>
<#--SNIP Code for navigation bar-->
<div class="container my-5">
    <div id="container">
        <#nested/>
    </div>
</div>
</body>
</html>
</#macro>
```

Now we define the sites themselves.
The anatomy of these freemarker templates contain different interesting parts.
The most straightforward one is `${product.name}`.
With this syntax we are reading the value of `name` of the model property `product`.
The next one is the [`#import` directive](https://freemarker.apache.org/docs/ref_directive_import.html). It makes macros and functions
defined in another file available within a custom namespace (`com` in our case).
With `<@com.page>` we are executing this macro.
The product detail page looks like this.

```injectedfreemarker
<#import "../fragments/shared-components.ftlh" as com>

<@com.page>
<h2>Single Product</h2>

<h3>${product.name}</h3>
<p>${product.desc}</p>
<p>${product.details}</p>
<p>${product.id}</p>
</@com.page>
```

There are other directives as well. With `<#list products as p>` we are invoking
the [`list` directive](https://freemarker.apache.org/docs/ref_directive_list.html) of freemarker to iterate over the
list stored in the model property `products`.

```injectedfreemarker
<#import "../fragments/shared-components.ftlh" as com>

<@com.page>
<h2>Products</h2>

    <#list products as p>
    <div class="card mb-3">
        <div class="card-body">
            <h5 class="card-title">${p.name}</h5>
            <h6 class="card-subtitle mb-2 text-muted">Id: ${p.id}</h6>
            <p class="card-text">${p.desc}</p>
            <a href="/products/${p.id}" class="card-link">Details</a>
        </div>
    </div>
    </#list>
</@com.page>
```

With the omitted navigation bar the final rendered results are looking like this:

*![products](https://media.graphassets.com/wWgF4lJcTRmZ3jRRRTkQ)Products overview*

*![product](https://media.graphassets.com/lbYj2N4QQy6A0qc8xrKf)Product detail view*

## Interactivity

This looks promising, but why does it feel like the beginning of the
internet again? On every click the whole page does a full reload
including header and all the resources. Also, the only loading indicator
is this small icon in the browser tab.
Of course for this app the loading times are trivial as we are effectively reading some bytes from memory and sending
them over a zero latency connection to the browser.
But let's assume this connection is slow and the business logic is complex.
So lets make this page "modern" and snappy again.

**_HTMX to the rescue._**

The basic principle of HTMX is, to replace a certain part of the
website with content received from the server. Traditionally when you click
on a link the server sends the whole new website over the wire, the
browser parses and renders it again from scratch. With HTMX we can tell
HTMX via the `hx-target` attribute on the link to just replace the
contents of the referenced html element with the response from the
webserver. This way the browser only needs to rerender this div and we
can also do nice things like displaying a custom loading spinner.

So how do I do this?

My app now has like two ~~hundred~~ links, do I really need to add these magical `hx-` attributes to all those links to
enable them for HTMX?
Thankfully no! There is an even more magical attribute called [`hx-boost`](https://htmx.org/attributes/hx-boost/). If
this attribute is
present on any parent element of an anchor (`<a>`) tag HTMX automagically does its magic.
To use HTMX we first need to add the webjar dependency `org.webjars.npm:htmx.org:1.9.11` to our `build.gradle` and
include it in the header of our page macro (`<script src="/webjars/htmx.org/dist/htmx.min.js"></script>`).
Lastly add the magic `hx-boost` to the body of the page template.

```html
<body
        hx-target="#container"
        hx-boost="true"
>
```

This snippet tells HTMX to replace the contents of the element with the id `container` with the response from
the server. Not only ids are supported but any CSS selectors.

So when we now click on the link to the product detail page HTMX replaces only the inner html of the `container` div
with the result from the server.

![htmx fail](https://media.graphassets.com/9cHbp82IQ4aunRoQdikZ)

*Insert facepalm gif*

The content div now contains the full website again, including the navbar. What went wrong?

Well our backend does not know anything about this partial update
mechanism and happily sends the whole template rendered again when
prompted. This leads to the fail you see above.

So how do we only send partial updates when necessary.
When HTMX makes requests it adds certain headers. On the backend side of things we can simply render
different templates depending on the presence of the most basic header
(`HX-Request=true`). HTMX also sends a [bunch of other
headers](https://htmx.org/docs/#request-headers) as well denoting the target element and other meta
information, so the opportunity exists to make this as complex as ~~possible~~ needed.
In code this looks like this:

```kotlin
@GetMapping("/products")
fun getProducts(): ModelAndView {
    return ModelAndView("sites/products", mapOf("products" to products))
}

@GetMapping("/products", headers = ["HX-Request=true"])
fun getProductsHtmx(): ModelAndView {
    return ModelAndView("fragments/products", mapOf("products" to products))
}
```

With the fragment (`fragments/products.fthl`) looking like this:

```injectedfreemarker
<h2>Products</h2>

<#list products as p>
<div class="card mb-3">
    <div class="card-body">
        <h5 class="card-title">${p.name}</h5>
        <h6 class="card-subtitle mb-2 text-muted">Id: ${p.id}</h6>
        <p class="card-text">${p.desc}</p>
        <a href="/products/${p.id}" class="card-link">Details</a>
    </div>
</div>
</#list>
```

The full page (`sites/products.ftlh`) simply includes the fragment within the page macro:

```injectedfreemarker
<#import "../fragments/shared-components.ftlh" as com>

<@com.page>
    <#include "../fragments/products.ftlh">
</@com.page>
```

The `hx-boost` attribute also changes the address bar to the URL which was called.
When correctly implemented on the server side, this leads to identical DOMs on page reload or bookmarks.

To better indicate an ongoing request we use the `hx-indicator`. This signals to HTMX that it should add a CSS class to an
element when requests are ongoing. In our case this simply leads to a spinner element of bootstrap to be shown.
See [global.css](https://github.com/mircohacker/Spring-Boot-and-HTMX/blob/main/src/main/resources/static/global.css)
for the extensive implementation 😉. This class could be used to implement any progress
indicator CSS is capable of. And if your want to do something CSS is incapable of you can use
the [events provided by HTMX](https://htmx.org/reference/#events) as an escape hatch.

### Error handling

When a HTMX boosted request fails for any reason, nothing visible happens.
No DOM update, no toast. Only a message in the browser console that a certain request failed.
To handle this case gracefully, HTMX provides the [response targets](https://htmx.org/extensions/response-targets/)
extension.
This extension allows us to use the response of any failing request and render it at any place in the DOM.
Just like the normal HTMX but for error responses instead.

To use it we have to include another script in our page macro.
The extension is distributed with HTMX so we do not need another webjar dependency.

We can simply add the script tag `<script src="/webjars/htmx.org/dist/ext/response-targets.js"></script>`.
We also need to explicitly enable the extension and tell it where to render our error responses.
To do this we add some more additional attributes to the body of our page.
The new body tag now looks like this:

```html
<body
        hx-target="#container"
        hx-boost="true"
        hx-indicator="#spinner"
        hx-ext="response-targets"
        hx-target-error="#any-errors"
>
```

As we are doing server side rendering this error messages have to be rendered on the server as well.
In this case we simply use the bootstrap toasts to display a short summary of the error.
This is done in this Controller

```kotlin
@ControllerAdvice
class MyErrorController {

    @ExceptionHandler(Exception::class)
    fun handleExceptions(
        e: Exception,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ModelAndView {
        val model: MutableMap<String, Any?> = mutableMapOf(
            "message" to e.message,
            "exception" to mapOf(
                "message" to e.message,
                "stacktrace" to e.stackTraceToString()
            )
        )
        val statusCode: HttpStatusCode = (if (e is ErrorResponse) e.statusCode else HttpStatus.INTERNAL_SERVER_ERROR)

        model["message"] = "Request to ${request.requestURI} failed with Code $statusCode"

        if (request.getHeader("HX-Request") == "true") {
            response.addHeader("HX-Reswap", "beforeend")
            response.addHeader("HX-Push-Url", "false")
            return ModelAndView("fragments/error", model, statusCode)
        }
        return ModelAndView("sites/error", model, statusCode)
    }
}
```

This controller defines the global exception handler for all types of exception.
First if extracts various values from the exception and the request and builds the model with it.
As our usual pattern we decide if this an HTMX request based on the header `HX-Request`.
If not a simple page is rendered from the model.
But if the request is an HTMX request we modify the behaviour of HTMX by setting certain response headers.

First with `HX-Reswap=beforeend` we tell HTMX to not replace the content of the defined error target, but instead append
the returned HTML at the end of the already present inner HTML.
This way the errors are stacking in the toast container until the user dismisses them.

With the second header (`HX-Push-Url=false`) we tell HTMX to not push the URL to the browser address bar and history.
This is done to keep the view in sync with the URL.
We do not replace the content of the page, so the URL should stay the same as well.

To demonstrate this behaviour I added some more entries to the navigation bar.

* The "Graceful error" renders a specific error template from within the controller. This could be used for specific
  business error only happening there.
* The "Uncaught exception" throws a not implemented exception. This path leads to the ErrorController above, which maps
  this exception to Status code 500 and renders the generic template.
* The "Not found" items maps to no controller or ressource at all. This prompts Spring to throw
  a `NoResourceFoundException` which is handled by the error controller as well. This class implements
  the `ErrorResponse` interface and therefor provides its own status code (404).

The resulting toasts look like this:

![stacked toasts](https://media.graphassets.com/V49NJPRvRfeFYIU0oRaF)

## What about more interactivity?

Of course javascript is not forbidden just because you started your application in the way described in this post.
In this sample I update the highlighted nav-bar element via a
simple hook whenever either a page loads or a HTMX request occurs. Naturally
this could also be achieved with serverside rendering. But why make it complicated if six
lines of Javascript are sufficient.
See the
file [navbar-highlighting.js](https://github.com/mircohacker/Spring-Boot-and-HTMX/blob/main/src/main/resources/static/navbar-highlighting.js)
for the implementation details.

Ok simple javascript snippets are possible, but what if my application has this one workflow,
where there is a lot of user interactivity wanted and beneficial?
Am I now cursed to abstain from js-frameworks altogether and implement everything with plain and painful javascript like
in the olden days?
Of course, it is possible to integrate a classic single page app with this setup.
I chose a vue application, but I am sure you can also transfer the important bits to your js framework of the ~~year~~
~~month~~ week.
The only requirement is, that it can be built to static files. So no Next.js et al.

For embedding this vue app into the otherwise pre rendered templates
there are three magic ingredients:

1. A `index.html` containing only the import of the main App. For a production build this renders to only the script
   bundle and the CSS bundle.
   See [vite.config.ts](https://github.com/mircohacker/Spring-Boot-and-HTMX/blob/main/vue-app/vite.config.ts) for how
   it is done exactly.

2. A simple [post build script](https://github.com/mircohacker/Spring-Boot-and-HTMX/blob/main/vue-app/scrips/moveBuildToSpringBoot.sh)
   which copies the files from the frontend build directory to the backend resources directory at
   the correct place.

3. The `main.ts` of the vue app only mounts the app when an event is
   received. This event is fired when the vue app fragment is rendered.
   This way we can define the time to render the app from the Spring Boot backend.

Lastly we include the necessary imports in our page macro and build the fragments to render the vue app on a specific
path.
No magic there.
I also added a navigation bar entry to navigate to the page rendering the vue app.

More on how to model the data flows between the vue app and the backend will be shown in a later blogpost.

## Next Steps

We are now at the end of what I wanted to show you today. Possible next steps are:

- Deployment: Currently the app only runs locally. In order to be useful at all, we need to deploy it, so users can reach it. 
  A follow-up post for this is already in planed.

- Improved embedded vue app. Currently, the vue app does not interact with the spring boot app at all. A mechanism to pass data to and from the vue app has to be developed. There is also a follow-up post planned for this.

- Testing: The current testing setup is lacking and also only works locally.
  For productive use all implemented endpoints have to be tested.

- Persistence: As you possibly already noticed, does this app not provide any means to add products.
  As our lambda has no way to store state between requests this storage has to implemented using external mechanisms
  like a database or some flat file in S3.
  As a first stop I would try an [AWS Dynamo simple table](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-resource-simpletable.html).

## Summary

It is absolutely possible to create a responsive and partially updating web application with Spring Boot and HTMX.
By the nature of using serverside rendering, a lot of business and display logic actually happens on the backend.
This must suit you if you want to enjoy this style of application.
It sure suits me.

The old "Right tool for the right job" proverb still holds true.
HTMX can enable you to shift a lot of logic to the backend and make the frontend ~~dumber~~ simpler.
But for some functions and use cases javascript is simply the better tool.
As we saw this does not necessarily require to a full-blown frontend framework.
We can use a javascript frameworks where it makes sense and stay with a simple serverside rendered app where it is not.