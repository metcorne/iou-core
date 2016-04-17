package nl.brusque.iou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Examples {
    @Test
    public void testCallWithSingleThen() {
        TestTypedIOU<Integer> iou = new TestTypedIOU<>();

        iou.getPromise()
                .then(new IThenCallable<Integer, Void>() {
                    @Override
                    public Void apply(Integer integer) throws Exception {
                        System.out.println(integer);

                        return null;
                    }
                });

        iou.resolve(42); // prints 42
    }

    @Test
    public void testPipedPromise() {
        TestTypedIOU<Integer> iou = new TestTypedIOU<>();

        iou.getPromise()
                .then(new IThenCallable<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer input) throws Exception {
                        return input * 10;
                    }
                })
                .then(new IThenCallable<Integer, String>() {
                    @Override
                    public String apply(Integer input) throws Exception {
                        return String.format("The result: %d", input);
                    }
                })
                .then(new IThenCallable<String, Void>() {
                    @Override
                    public Void apply(String input) throws Exception {
                        System.out.println(input);

                        return null;
                    }
                });

        iou.resolve(42); // prints "The result: 420"
    }

    @Test
    public void testSequentialPromise() {
        TestTypedIOU<Integer> iou = new TestTypedIOU<>();

        TestTypedPromise<Integer> promise = iou.getPromise();

        promise
                .then(new IThenCallable<Integer, Void>() {
                    @Override
                    public Void apply(Integer input) throws Exception {
                        System.out.println(input);

                        return null;
                    }
                });

        promise
                .then(new IThenCallable<Integer, String>() {
                    @Override
                    public String apply(Integer input) throws Exception {
                        String result = String.format("%d * 10 = %d", input, input * 10);
                        System.out.println(result);

                        return result;
                    }
                });

        iou.resolve(42); // prints "42" and "42 * 10 = 420" in exactly this order
    }

    @Test
    public void testRejectPromise() {
        TestTypedIOU<Integer> iou = new TestTypedIOU<>();

        iou.getPromise()
                .then(new IThenCallable<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer * 42;
                    }
                }, new IThenCallable<Object, Integer>() {
                    @Override
                    public Integer apply(Object reason) throws Exception {
                        System.out.println(String.format("The promise was rejected, because %s", reason));

                        return null;
                    }
                });

        iou.reject("his name was Robert Paulson"); // prints "The promise was rejected, because his name was Robert Paulson"
    }

    @Test
    public void testFailingPromise() {
        TestTypedIOU<Integer> iou = new TestTypedIOU<>();

        iou.getPromise()
                .then(new IThenCallable<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer input) throws Exception {
                        throw new Exception("I felt like destroying something beautiful");
                    }
                })
                .then(new IThenCallable<Integer, Void>() {
                    @Override
                    public Void apply(Integer somethingBeautiful) throws Exception {
                        System.out.println(String.format("This is beautiful: %d", somethingBeautiful));

                        return null;
                    }
                } ,new IThenCallable<Object, Void>() {
                    @Override
                    public Void apply(Object reason) throws Exception {
                        System.out.println(String.format("The promise was rejected, because %s", ((Exception)reason).getMessage()));

                        return null;
                    }
                });

        iou.resolve(42); // prints "The promise was rejected, because I felt like destroying something beautiful"
    }
}
