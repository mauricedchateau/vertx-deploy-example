vertx-deploy-example
====================

Illustrates possible threading issue.

The verticle programmatically deploys a module (the mod-mongo-persistor is used for this, as a freely available example).  
A <code>CountDownLatch</code> is used to force the verticle code to wait before the last part of the <code>start()</code> method is run;
it is initialized with '1' before the <code>deployModule(...)</code> call, and it is counted down inside the * asynchronous* callback method.

However, the timeout of the latch occurs (regardsless of the chosen timeout period) before the callback is called, **and** the callback is
eventually called by the same thread as the one running the verticle's <code>start()</code> method (which starts the pleoyment to start with).  
Is the asynchronous behaviour exhausting the event loop thread pool?

Steps to reproduce:

- vertx run com/acme/DeployerExample.java
