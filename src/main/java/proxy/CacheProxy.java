package proxy;

import annotations.Cache;
import data.Employee;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;

import static java.lang.ClassLoader.getSystemClassLoader;

/**
 * Created by ABurykin on 24.08.2016.
 */
public class CacheProxy implements InvocationHandler {

    private final Object realServiceDBImpl;
    String rootDirectory;

    ArrayList<Employee> jvmCache = null;


    public CacheProxy(Object realServiceDBImpl, String rootDirectory) { // , String[] cachePropertiesDefault
        this.realServiceDBImpl = realServiceDBImpl;
        this.rootDirectory = rootDirectory;


    }

    // возвращает динамический прокси
    public static <T> T cache(Object delegate, String rootDirectory) {
        return (T) Proxy.newProxyInstance(getSystemClassLoader(),
                delegate.getClass().getInterfaces(),
                new CacheProxy(delegate, rootDirectory)
        );
    }

    // перехватчик методов
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("\nCacheProxy перехватил вызов метода: " + method.getName()); // runService
        Method currMethod = method;
        Cache cacheAnnotaion =  currMethod.getAnnotation(Cache.class); //currMethod.getClass().getAnnotation(Cache.class);

        Object result = null;
        if (cacheAnnotaion != null) {  // значит метод нужно кэшировать
            // получаю параметры кэша
            System.out.println("\nПараметры кэша:");
            Cache.CacheType cacheType = cacheAnnotaion.cacheType();
           // int[] ignoreParams = cacheAnnotaion.ignoreParams();
            int listSize = cacheAnnotaion.listSize();
            String fileName = "UseMethodName".equals(cacheAnnotaion.fileName())?currMethod.getName():cacheAnnotaion.fileName();
            boolean zip = cacheAnnotaion.zip();

            System.out.println("cacheAnnotaion = " + cacheType);
            System.out.println("zip = " + zip);
            System.out.println("fileName = " + fileName);

           // System.out.println("ignoreParams = " + ignoreParams);
            System.out.println("listSize = " + listSize);



            if (cacheType == Cache.CacheType.FILE)
            {
                String checkFile = rootDirectory + fileName + (zip?".zip":"");
                // ищу файл в кэше
                if (checkCache(checkFile)){
                  //  ZipOutputStream
                    result =  getSerializationResult( fileName,  zip,  rootDirectory);
                }
                else // нет сериализованного метода
                {
                    result = method.invoke(realServiceDBImpl, args); // выполнить реальный метод
                    setSerializationResult( (ArrayList<Employee>)result,   fileName,  listSize,  zip,  rootDirectory);
                }

            }
            else if (cacheType == Cache.CacheType.JVM)
            {

            }

           // result = invokeCachedMethod(currMethod, cacheAnnotaion);
        } else {
            result = method.invoke(realServiceDBImpl, args); // выполнить реальный метод
        }
        System.out.println("CacheProxy завершил метод: " + method.getName());
        return result;

    }

    // true если файл найден
    private boolean checkCache(String checkFile){
             File file = new File(checkFile);
             System.out.println( checkFile + " exists = " + file.exists());
             return file.exists();
     }

    private void setSerializationResult(ArrayList<Employee> result,  String fileName, int listSize, boolean zip, String rootDirectory){

        // создаю кэшированный объект
        ArrayList<Employee> saveObject = new  ArrayList<Employee>();
        if (listSize == -1) saveObject = result;
        else {
            for (Employee employee : result) {

                if (listSize == 0) break;
                listSize--;
                saveObject.add(employee);
            }
        }

        // кэширую
        try {
            FileOutputStream fos;
            if (zip){
                    System.out.println("Кэширую результат в "+ fileName+".zip");
                    fos = new FileOutputStream( new File(rootDirectory+fileName+".zip"));
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    ObjectOutputStream oos = new ObjectOutputStream(zos);
                    oos.writeObject(saveObject);
                    oos.flush();
                    oos.close();
            } else { // сохранил объект в файл
                    System.out.println("Кэширую результат в "+ fileName);
                    fos = new FileOutputStream( new File(rootDirectory+fileName));
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

    private ArrayList<Employee> getSerializationResult(String fileName, boolean zip, String rootDirectory){

        ArrayList<Employee> result = new ArrayList<Employee>();
        String file = rootDirectory+fileName;

        try {
            if (zip){
                System.out.println("Получаю результат из "+ rootDirectory+fileName+".zip");


            }
            else {
                System.out.println("Получаю результат из "+ rootDirectory+fileName);
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fis);
                result = ( ArrayList<Employee>) oin.readObject();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
            catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }


    private void saveResultInJVM(){}



}
