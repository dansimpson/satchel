# Satchel
Asset processing and packaging pipeline for Scala and other JVM based applications.  Use satchel
to develop javascript heavy web applications without worrying about css and javascript request overhead.

##Features
* Directory walking to include all files you want
* Javascript concatenation and compression with YUI
* CSS concatenation and compression with YUI
* On the fly CoffeeScript compilation
* On the fly Less CSS compilation
* On the fly Javascript template wrapping
* Caching for production environments


##Javascript satchels
  val core = new Satchel(
    name = "core",
    root = "webapp/javascripts/vendor",
    patterns = List(
      "/jquery.js",
      "/underscore.js",
      "/backbone.js",
      "/lib/**/*.js"
    )
  )
  
  // yields combined and processed results
  core.processed 
  
The above code registers a satchel by the name of core, and includes
jquery, underscore, backbone, and all js files in the lib sub-directory.

##Production/Development mode

  // enable dev mode
  // no caching, no compression
  Satchel.devmode = true
  
  // disable dev mode
  // compressed and cached
  Satchel.devmode = false
  

##Javascript template satchels
  val templates = new Satchel(
    name = "templates",
    root = "webapp/javascripts/templates",
    mime = "jst",
    patterns = List(
      "/**/*.jst",
      "/**/*.mustache"
    )
  )
  
The above will generate a wrapped JS object for each found template.
If for example, a file called index.jst existed in the root directory,
we would access the template in javascript via:

  var index = JST["/index.jst"]
  
  // or a nested mustache template
  var other = JST["/subdirectory/other.mustache"]
  
##CoffeeScript satchels
  val coffee = new Satchel(
    name = "coffee",
    root = "webapp/coffeescripts",
    mime = "coffee",
    patterns = List(
      "/**/*.coffee"
    )
  )
  
Note: coffee-script.js must exist in your classpath, you can get it here: https://github.com/jashkenas/coffee-script/tree/master/extras
  
##Running with Scalatra
Simply add the servlet to your servlet container.
  
  context.addServlet(new ServletHolder(new SatchelServlet()), "/bundles/*")
  
Now you can access your satchels via /bundles/core.js or /bundles/coffee.coffee
 
