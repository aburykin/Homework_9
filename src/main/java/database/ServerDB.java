package database;

import annotations.Cache;
import data.Employee;

import java.util.Date;
import java.util.List;

/**
 * Created by ABurykin on 24.08.2016.
 */


public interface ServerDB {

    @Cache(zip = true, fileName = "last_backup")
    List<Employee> runBackup();

    @Cache(cacheType = Cache.CacheType.FILE, fileName = "testFile_getOnlyFemale", listSize = 2)  //(cacheType = Cache.CacheType.FILE , zip=true) //
    List<Employee> getOnlyFemale();

    @Cache(cacheType = Cache.CacheType.JVM)
    List<Employee> getOnlyMale();

    List<Employee> getOnlyRichEmployee(int salary); // for example: salary >= 100_000

    List<Employee> select(String name, int age, boolean gender, double salary);

}
