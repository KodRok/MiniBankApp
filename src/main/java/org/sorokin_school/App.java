package org.sorokin_school;

import org.sorokin_school.config.AppConfig;
import org.sorokin_school.console.ConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);

        ConsoleListener listener = context.getBean(ConsoleListener.class);
        listener.start();

        System.out.println("Закрытие Spring-контекста...");
        context.close();
    }
}
