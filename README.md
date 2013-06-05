vertx-deploy-example
====================

Illustrates threading misconception. **Don't schedule work inside a verticle this way!**

The verticle programmatically deploys a module (the mod-mongo-persistor is used for this, as a freely available example).  
A <code>CountDownLatch</code> is used to force the verticle code to wait before the last part of the <code>start()</code> method is run;
it is initialized with '1' before the <code>deployModule(...)</code> call, and it is counted down inside the *asynchronous* callback method.

However, that callback (and subsequently the <code>countDown()</code> method) will not be called before the latch times out;
as verticles are **single-threaded** (and this includes running callbacks), the <code>await(...)</code> method blocks the **designated thread** from
running the callback method.  
Only when the thread is set free (i.e. when the latch timed out and the rest of the <code>start()</code> method has completed) the asynchronous handler
method will be called.

Steps to run:
-------------
- vertx run com/acme/DeployerExample.java

The log output will demonstrate that the order in which the various parts of the code are run differs from what one might expect with the wrong assumptions
with regards to threading and asyncronicity in vert.x. It also shows that the various parts within a verticle are run by the same (event pool) thread.
