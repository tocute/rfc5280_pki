/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twca.ca;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 *
 * @author bill.chang
 */
@Controller
@RequestMapping(value = "/")
public class HelloController {
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String home(Locale locale, Map<String,Object> model) {
     //System.logger.info("Welcome home! The client locale is {}.", locale);
     System.out.println("Welcome home! The client locale is {}.");

     Date date = new Date();
     DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

     String formattedDate = dateFormat.format(date);
     //model.addAttribute("serverTime", formattedDate );
     model.put("serverTime", formattedDate);

     return "index";
    }
    
    @RequestMapping(value = "index_model", method = RequestMethod.GET)
    public String home_model(Locale locale, Model model) {
     //System.logger.info("Welcome home! The client locale is {}.", locale);
     System.out.println("Welcome home! The client locale is {}.");

     Date date = new Date();
     DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

     String formattedDate = dateFormat.format(date);
     model.addAttribute("serverTime", formattedDate );

     return "index";
    }
    
    @RequestMapping(value = "best", method = RequestMethod.GET, params = {"account", "password"})
    public String best(
            Locale locale, 
            Model model,
            @RequestParam(value = "account") String account, 
            @RequestParam(value = "password") String password) 
    {
     //System.logger.info("Welcome home! The client locale is {}.", locale);
     System.out.println("===> best");

     if("Bill".equals(account) == true)
         model.addAttribute("loginMessage", "比爾在學習" );
     else
         model.addAttribute("loginMessage", "陌生人" );

     return "best";
    }
}

