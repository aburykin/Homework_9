
import database.ServerDB;
import database.ServerDBImpl;

import static proxy.CacheProxy.cache;

/**
 * Created by ABurykin on 23.08.2016.
 */
public class Main {
    public static void main(String[] args) throws NoSuchMethodException {

        String rootDir = System.getProperty("user.dir")+ "/src/main/resources/cache_results/";
        //System.out.println("rootDir = " + rootDir);
        ServerDB proxyDB = cache(new ServerDBImpl(),rootDir);

        ServerDBImpl.printResult( proxyDB.getOnlyFemale() );

       // ServerDBImpl.printResult( proxyDB.runBackup() );

    }
}


/*
        TestClass x  =  new TestClass();
        x.myMethod();
*/