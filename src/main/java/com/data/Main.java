package com.data;

import javax.jdo.*;
import javax.jdo.query.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("JDO + DataNucleus demo start");

        // JDOHelper 自动读取 classpath 下 META-INF/persistence.xml 中的 PersistenceUnit
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("Tutorial");
        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();

            // 新增两条数据
            Person p1 = new Person("Alice", 30);
            Person p2 = new Person("Bob", 25);
            pm.makePersistent(p1);
            pm.makePersistent(p2);

            tx.commit();
            System.out.println("Persisted: " + p1 + " and " + p2);
        } finally {
            if (tx.isActive()) tx.rollback();
        }

        // 查询
        try {
            tx = pm.currentTransaction();
            tx.begin();

            // JDOQL 简单查询：检索所有 Person
            Extent<Person> extent = pm.getExtent(Person.class, true);
            for (Person p : extent) {
                System.out.println("> extent: " + p);
            }

            // 或使用查询 API
            Query<Person> q = pm.newQuery(Person.class, "age >= minAge");
            q.declareParameters("int minAge");
            @SuppressWarnings("unchecked")
            List<Person> results = (List<Person>) q.execute(25);
            System.out.println("Query results (age >= 25):");
            for (Person p : results) System.out.println(" - " + p);

            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            pm.close();
            pmf.close();
        }

        System.out.println("JDO demo finished");
    }
}
