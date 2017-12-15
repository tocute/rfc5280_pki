/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twca.hello;

/**
 *
 * @author bill.chang
 */
public class HelloWorld {
   private String message1;
   private String message2;

   public void setMessage1(String message){
      this.message1 = message;
   }
   public void setMessage2(String message){
      this.message2 = message;
   }
   public void getMessage1(){
      System.out.println("World Message1 : " + message1);
   }
   public void getMessage2(){
      System.out.println("World Message2 : " + message2);
   }
}
