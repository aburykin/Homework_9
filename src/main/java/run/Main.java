package run;

import database.ServerDB;
import database.ServerDBImpl;
import proxy.CacheProxy;

/**
 * Created by ABurykin on 23.08.2016.
 */
public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
        String rootDir = System.getProperty("user.dir") + "/src/main/resources/cache_results/";
        System.out.println("rootDir = " + rootDir);

        // тестирую сохранение в файл и zip

        ServerDB proxyDB = CacheProxy.cache(new ServerDBImpl(),rootDir);

      //  ServerDBImpl.printResult(proxyDB.getOnlyFemale());
      //  ServerDBImpl.printResult(proxyDB.getOnlyFemale());

       // ServerDBImpl.printResult(proxyDB.runBackup());
       // ServerDBImpl.printResult(proxyDB.runBackup());

       // ServerDBImpl.printResult(proxyDB.getOnlyMale());
       // ServerDBImpl.printResult(proxyDB.getOnlyMale());

        // ServerDBImpl.printResult(proxyDB.getOnlyRichEmployee(100000));
        // ServerDBImpl.printResult(proxyDB.getOnlyRichEmployee(100000));

        ServerDBImpl.printResult(proxyDB.select("Гена",19,true,777));
        ServerDBImpl.printResult(proxyDB.select("Катя",19,false,777)); // должен снова вернуться список из 3-х человек


    }
}