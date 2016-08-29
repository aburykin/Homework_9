package database;

import annotations.Cache;
import data.CacheType;
import data.Employee;

import java.util.List;

/**
 * Created by ABurykin on 24.08.2016.
 */


public interface ServerDB {

    @Cache(zip = true, fileName = "last_backup", listSize = 4)
    List<Employee> runBackup();

    @Cache(cacheType = CacheType.FILE, fileName = "female.ser", listSize = 2)
    List<Employee> getOnlyFemale();

    @Cache(cacheType = CacheType.JVM)
    List<Employee> getOnlyMale();

    @Cache(cacheType = CacheType.JVM, listSize = 1)
    List<Employee> getOnlyRichEmployee(int salary);

    @Cache(cacheType = CacheType.JVM, ignoreParams = {String.class, Boolean.class})
    List<Employee> select(String name, int age, boolean gender, double salary);

}
