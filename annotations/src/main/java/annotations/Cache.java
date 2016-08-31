package annotations;

import data.CacheType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ABurykin on 24.08.2016.
 */

@Target(value = ElementType.METHOD) // назначаю область, на что эту аннотацию можно вешать
@Retention(value = RetentionPolicy.RUNTIME)
// указываю время жизни аннотации, в данном случае она будет видна в процессе выполнения
public @interface Cache {

    CacheType cacheType() default CacheType.FILE;   // тип кэширования файл или JVM

    boolean zip() default false;                    // сжимать кэш или не сжимать

    String fileName() default "";                   // файл в котором буду сохраняться кэшированные результаты;

    Class[] ignoreParams() default {};                // типы параметров метода, которые нужно игнорировать при кэшировании;

    int listSize() default -1;                      // количество элементов, которые нужно кэшировать;  -1 сохранает результат полностью

}
