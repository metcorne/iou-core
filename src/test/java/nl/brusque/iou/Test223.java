package nl.brusque.iou;

import nl.brusque.iou.minimocha.MiniMochaDescription;
import nl.brusque.iou.minimocha.MiniMochaRunner;
import nl.brusque.iou.minimocha.MiniMochaSpecificationRunnable;
import org.junit.Assert;
import org.junit.runner.RunWith;

import static nl.brusque.iou.Util.*;


@RunWith(MiniMochaRunner.class)
public class Test223 extends MiniMochaDescription {
    public Test223() {
        super("2.2.3: If `onRejected` is a function,", new IOUMiniMochaRunnableNode() {
            final String dummy = "DUMMY";

            @Override
            public void run() {
                describe("2.2.3.1: it must be called after `promise` is rejected, with `promise`’s rejection reason as its first argument.", new Runnable() {
                    @Override
                    public void run() {
                        testRejected(dummy, new Testable<String>() {
                            @Override
                            public void run(final TestableParameters parameters) {
                                AbstractPromise<String> promise = parameters.getPromise();

                                allowMainThreadToFinish();
                                promise.then(null, new TestThenCallable<Object, Void>() {
                                    @Override
                                    public Void apply(Object o) throws Exception {
                                        Assert.assertEquals(o, dummy);
                                        parameters.done();

                                        return null;
                                    }
                                });
                            }
                        });
                    }
                });

                describe("2.2.3.2: it must not be called before `promise` is rejected", new Runnable() {
                    @Override
                    public void run() {


                    specify("rejected after a delay", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        final AbstractIOU<String> d = deferred();
                        final boolean[] isRejected = {false};

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                allowMainThreadToFinish();
                                Assert.assertTrue("isRejected should be true", isRejected[0]);
                                done();

                                return null;
                            }
                        });

                        delayedCall(new Runnable() {
                            @Override
                            public void run() {
                                d.reject(dummy);
                                isRejected[0] = true;
                            }
                        }, 50);
                        }
                    });

                    specify("never rejected", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        AbstractIOU<String> d = deferred();
                        final boolean[] onRejectedCalled = {false};

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                Assert.assertTrue("isRejected should be true", onRejectedCalled[0]);
                                done();

                                return null;
                            }
                        });

                        delayedCall(new Runnable() {
                            @Override
                            public void run() {
                                Assert.assertFalse("onRejected should not have been called", onRejectedCalled[0]);
                                done();
                            }
                        }, 150);

                        }
                    });
                    }
                });

                describe("2.2.3.3: it must not be called more than once.", new Runnable() {
                    @Override
                    public void run() {
                    specify("already-rejected", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        final int[] timesCalled = {0};

                        AbstractPromise<String> d = rejected(dummy);

                        d.then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[0]);
                                done();

                                return null;
                            }
                        });
                        }
                    });

                    specify("trying to reject a pending promise more than once, immediately", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        AbstractIOU<String> d = deferred();
                        final int[] timesCalled = {0};

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[0]);
                                done();

                                return null;
                            }
                        });

                        d.reject(dummy);
                        d.reject(dummy);
                        }
                    });

                    specify("trying to reject a pending promise more than once, delayed", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        final AbstractIOU<String> d = deferred();
                        final int[] timesCalled = {0};

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[0]);
                                done();

                                return null;
                            }
                        });

                        delayedCall(new Runnable() {
                            @Override
                            public void run() {
                                d.reject(dummy);
                                d.reject(dummy);
                            }
                        }, 50);

                        }
                    });

                    specify("trying to reject a pending promise more than once, immediately then delayed", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        final AbstractIOU<String> d = deferred();
                        final int[] timesCalled = {0};

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[0]);
                                done();

                                return null;
                            }
                        });

                        d.reject(dummy);
                        delayedCall(new Runnable() {
                            @Override
                            public void run() {
                                d.reject(dummy);
                            }
                        }, 50);
                        }
                    });

                    specify("when multiple `then` calls are made, spaced apart in time", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        final AbstractIOU<String> d = deferred();
                        final int[] timesCalled = {0, 0, 0};

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[0]);

                                return null;
                            }
                        });

                        delayedCall(new Runnable() {
                            @Override
                            public void run() {
                                d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                                    @Override
                                    public Void apply(Object o) {
                                        Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[1]);

                                        return null;
                                    }
                                });
                            }
                        }, 50);

                        delayedCall(new Runnable() {
                            @Override
                            public void run() {
                                d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                                    @Override
                                    public Void apply(Object o) {
                                        Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[2]);
                                        done();

                                        return null;
                                    }
                                });
                            }
                        }, 100);

                        delayedCall(new Runnable() {
                            @Override
                            public void run() {
                                d.reject(dummy);
                            }
                        }, 150);
                        }
                    });

                    specify("when `then` is interleaved with rejection", new MiniMochaSpecificationRunnable() {
                        @Override
                        public void run() {
                        AbstractIOU<String> d = deferred();
                        final int[] timesCalled = {0, 0};

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[0]);

                                return null;
                            }
                        });

                        d.reject(dummy);

                        d.getPromise().then(null, new TestThenCallable<Object, Void>() {
                            @Override
                            public Void apply(Object o) {
                                allowMainThreadToFinish();
                                Assert.assertEquals("timesCalled should be 0", 1, ++timesCalled[1]);
                                done();

                                return null;
                            }
                        });
                        }
                    });
                    }
                });
            }
        });
    }
}