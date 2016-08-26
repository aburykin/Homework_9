package proxy;

import annotations.Cache;
import data.CacheType;
import data.Employee;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.lang.ClassLoader.getSystemClassLoader;

/**
 * Created by ABurykin on 24.08.2016.
 */
public class CacheProxy implements InvocationHandler {

    private final Object realServiceDBImpl;
    String rootDirectory;

    Map<String, ArrayList> jvmCache = new HashMap<String, ArrayList>();

    public CacheProxy(Object delegate, String rootDirectory) {
        this.realServiceDBImpl = delegate;
        this.rootDirectory = rootDirectory;
    }

    public static <T> T cache(Object delegate, String rootDirectory) {
        return (T) Proxy.newProxyInstance(getSystemClassLoader(),
                delegate.getClass().getInterfaces(),
                new CacheProxy(delegate, rootDirectory)
        );
    }

    // перехватчик методов
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("\nCacheProxy перехватил вызов метода: " + method.getName()+"  ------------------------------------------------------------------------------------------");
        Cache cacheAnnotaion = method.getAnnotation(Cache.class);
        Object result = invokeCachedMethod(method, args, cacheAnnotaion);
        System.out.println("\nCacheProxy завершил метод: " + method.getName());
        return result;
    }

    private Object invokeCachedMethod(Method method, Object[] args, Cache cacheAnnotaion) throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        if (cacheAnnotaion != null) {
            showCacheParams(cacheAnnotaion, method);

            CacheType cacheType = cacheAnnotaion.cacheType();
            if (cacheType == CacheType.FILE) {
                result = getCachedFile(method, args, cacheAnnotaion);
            } else if (cacheType == CacheType.JVM) {
                result = getSavingResaultFromJVM(method, args, cacheAnnotaion);
                // result = method.invoke(realServiceDBImpl, args);// загрулшка
            }
        } else {
            result = method.invoke(realServiceDBImpl, args);
        }
        return result;
    }

    private Object getCachedFile(Method method, Object[] args, Cache cacheAnnotaion) throws IllegalAccessException, InvocationTargetException {
        String fileName = getCacheFileName(cacheAnnotaion, method);
        fileName = addIgnoreParamsToFilename(args, cacheAnnotaion, fileName);
        boolean zip = cacheAnnotaion.zip();
        int listSize = cacheAnnotaion.listSize();

        Object result;
        if (checkCacheFile(rootDirectory + fileName + (zip ? ".zip" : ""))) {
            result = getSerializationResult(fileName, zip, rootDirectory);
        } else {
            result = method.invoke(realServiceDBImpl, args);
            setSerializationResult((ArrayList<Employee>) result, fileName, listSize, zip, rootDirectory);
        }
        return result;
    }

    private void showCacheParams(Cache cacheAnnotaion, Method method){
        System.out.println("\nПараметры кэша:");
        System.out.println("cacheAnnotaion = " + cacheAnnotaion.cacheType());
        System.out.println("zip = " + cacheAnnotaion.zip());
        System.out.println("fileName = " + getCacheFileName(cacheAnnotaion, method));
        System.out.println("listSize = " + cacheAnnotaion.listSize());

        Class[] classes = cacheAnnotaion.ignoreParams();
        String listClasses = "";
        for (Class x : classes) listClasses += x+",";

        System.out.println("ignoreParams = " + listClasses+"\n");
    }



    private String getCacheFileName(Cache cacheAnnotaion, Method method) {
        return "".equals(cacheAnnotaion.fileName()) ? method.getName() : cacheAnnotaion.fileName();
    }

    private boolean checkCacheFile(String checkFile) {
        File file = new File(checkFile);
        return file.exists();

    }

    private void setSerializationResult(ArrayList<Employee> result, String fileName, int listSize, boolean zip, String rootDirectory) {
        ArrayList<Employee> saveObject = createSavingResult(result, listSize);

        try {
            FileOutputStream fos;
            if (zip) {
                fileName += ".zip";
                System.out.println("Кэширую результат в " + fileName);
                fos = new FileOutputStream(new File(rootDirectory + fileName));
                GZIPOutputStream  zip_os = new GZIPOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(zip_os);
                oos.writeObject(saveObject);
                oos.flush();
                oos.close();
            } else {
                System.out.println("Кэширую результат в " + fileName);
                fos = new FileOutputStream(new File(rootDirectory + fileName));
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(saveObject);
                oos.flush();
                oos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Employee> createSavingResult(ArrayList<Employee> result, int listSize) {
        ArrayList<Employee> saveObject = new ArrayList<Employee>();
        if (listSize == -1) saveObject = result;
        else {
            for (Employee employee : result) {
                if (listSize == 0) break;
                listSize--;
                saveObject.add(employee);
            }
        }
        return saveObject;
    }

    private ArrayList<Employee> getSerializationResult(String fileName, boolean zip, String rootDirectory) {

        ArrayList<Employee> result = new ArrayList<Employee>();
        String file = rootDirectory + fileName;

        try {
            if (zip) {
                file +=".zip";
                System.out.println("Получаю результат из " + file);
                FileInputStream fis = new FileInputStream(file);
                GZIPInputStream zip_is = new GZIPInputStream(fis);
                ObjectInputStream oin = new ObjectInputStream(zip_is);
                result = (ArrayList<Employee>) oin.readObject();
            } else {
                System.out.println("Получаю результат из " + file);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fis);
                result = (ArrayList<Employee>) oin.readObject();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }



    private Object getSavingResaultFromJVM(Method method, Object[] args, Cache cacheAnnotaion) throws IllegalAccessException, InvocationTargetException {
        String fileName = getCacheFileName(cacheAnnotaion, method);
        int listSize = cacheAnnotaion.listSize();
        fileName = addIgnoreParamsToFilename(args, cacheAnnotaion, fileName);

        Object result;
        if (checkJVMCache(fileName)) {
            result = getResultFromJVM(fileName);
        } else {
            result = method.invoke(realServiceDBImpl, args);
            saveResultInJVM((ArrayList<Employee>) result, fileName, listSize);
        }
        return result;
    }

    private String addIgnoreParamsToFilename(Object[] args, Cache cacheAnnotaion, String fileName) {
        for (Object arg : args) {
            boolean contains = false;
            for (Class aClass : cacheAnnotaion.ignoreParams()) {
              if (arg.getClass() == aClass) {
                  contains = true;
              }
            }
           if (!contains) fileName += "_"+arg;
        }
       // System.out.println("!! fileName = " + fileName);
        return fileName;
    }

    private boolean checkJVMCache(String fileName){
        return jvmCache.get(fileName) == null? false : true;
    }

    private void saveResultInJVM(ArrayList<Employee> result, String fileName, int listSize) {
        jvmCache.put(fileName, createSavingResult( result,  listSize));
    }

    private ArrayList getResultFromJVM(String fileName) {
        return jvmCache.get(fileName);
    }


}
