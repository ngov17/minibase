/*-------------------------------------------------------------------------
 *
 * Executor.java
 *  Takes in an abstract syntax tree, executes it
 *
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/executor/Executor.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.executor;

import com.minibase.access.HeapFile;
import com.minibase.access.Meta;
import com.minibase.access.Record;
import com.minibase.parser.*;
import com.minibase.storage.FileManager;

import java.util.Scanner;

public class Executor {

    public static void main(String[] args) {
        FileManager fm = new FileManager("meta_table");
        if (!fm.exists) Meta.initMetaFiles();
        else Meta.loadMeta();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">> ");
            String command = scanner.nextLine();
            if(command.equals("test")) {
                HeapFile test = new HeapFile("test");
                System.out.println(test.isEmpty());
                //Record rec = test.getFirstRecord();
                //System.out.println(test.hasNext());

                while(test.hasNext()) {
                    Record rec = test.getNextRecord();
                    for(Object o : rec.values) {
                        System.out.print(o);
                        System.out.print(" | ");
                    }
                    System.out.println("    -----------    ");
                }
                test.close();
                continue;
            }
            try {

                ASTNode node = Parser.parse(command);



                switch (node.id) {
                    case "CREATE":
                        CreateTable c_exec_plan = new CreateTable((ASTCreate) node);
                        c_exec_plan.execute();
                        System.out.println("Table Successfully Created");
                        break;

                    case "INSERT":
                        Insert i_exec_plan = new Insert((ASTInsert) node);
                        i_exec_plan.execute();
                        System.out.println("INSERT SUCCESSFUL");
                        break;

                    case "SELECT":
                        Select s_exec_plan = new Select((ASTSelect) node);
                        s_exec_plan.execute();
                        break;

                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }


    }
}