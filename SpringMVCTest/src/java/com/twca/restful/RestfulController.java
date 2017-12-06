/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twca.restful;

import com.twca.exception.SpringException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.json.JSONObject;

/**
 *
 * @author bill.chang
 */
@Controller
@RequestMapping("/restful")
public class RestfulController { 
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String index()
    {
        System.out.println("Show ");
        return "restful";
    }

    @ResponseBody
    @RequestMapping(value="/user/{id}",method=RequestMethod.GET, produces = "application/json")
    public String read(@PathVariable("id") Integer id,HttpServletRequest req, HttpSession session)
    {
        //Integer pid = Integer.valueOf(req.getParameter("id"));
        System.out.println("GET "+id);
        Map<String, Object> data = new HashMap<>();
        data.put( "ReturnCode", 0 );
        data.put( "Message", id+ " GET_OK");

        JSONObject json = new JSONObject(data);
        return json.toString();
    }
    
    @ResponseBody
    @RequestMapping(value="/user/{id}",method=RequestMethod.POST, produces = "application/json")
    public String create(@PathVariable("id") Integer id)
    {
        System.out.println("POST "+id);
        Map<String, Object> data = new HashMap<>();
        data.put( "ReturnCode", 0 );
        data.put( "Message", id+ " POST_OK");

        JSONObject json = new JSONObject(data);
        return json.toString();
    }
    
    @ResponseBody
    @RequestMapping(value="/user/{id}",method=RequestMethod.PUT, produces = "application/json")
    public String update(@PathVariable("id") Integer id)
    {
        System.out.println("PUT "+id);
        Map<String, Object> data = new HashMap<>();
        data.put( "ReturnCode", 0 );
        data.put( "Message", id+ " PUT_OK");

        JSONObject json = new JSONObject(data);
        return json.toString();
    }
    
    @ResponseBody
    @RequestMapping(value="/user/{id}",method=RequestMethod.DELETE, produces = "application/json")
    public String delete(@PathVariable("id") Integer id)
    {
        System.out.println("DELETE "+id);
        
        Map<String, Object> data = new HashMap<>();
        data.put( "ReturnCode", 0 );
        data.put( "Message", id+ " DELETE_OK");

        JSONObject json = new JSONObject(data);
        return json.toString();
    }
    
    @RequestMapping(value="/user1",method=RequestMethod.GET)
    //http://localhost:8080/WebApplication1/restful/user1?name=aaa&age=29
    public String toUser(String name,int age) throws Exception
    {
        if(age < 20)
        {   
            System.out.println("GET toUSer age" + age);
            throw new SpringException("Given age is too small");
        }
        else if(age > 100)
        {
            System.out.println("Given age is too Large" + age);
            throw new Exception("Given age is too Large");
        }
        System.out.println("GET toUSer " + name + " " + age);
        return "restful";
    }
    //auto package
    @RequestMapping(value="/user2",method=RequestMethod.GET)
    public String toUser(User u){
        System.out.println("GET toUSer " + u.getName()+" "+ u.getAge());
        return "restful";
    }
    
    @ResponseBody
    @RequestMapping(value="/upload",method=RequestMethod.POST, produces = "application/json")
    public String upload(HttpServletRequest req) throws Exception{
        
        MultipartHttpServletRequest mreq = (MultipartHttpServletRequest)req;
        MultipartFile file = mreq.getFile("file");
        String fileName = file.getOriginalFilename();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String finalFileName = sdf.format(new Date())+fileName.substring(fileName.lastIndexOf('.'));
        FileOutputStream fos = new FileOutputStream(req.getSession().getServletContext().getRealPath("/")+
                "upload/"+finalFileName);
        fos.write(file.getBytes());
        fos.flush();
        fos.close();

        Map<String, Object> data = new HashMap<>();
        data.put( "ReturnCode", 0 );
        data.put( "URL", "../upload/"+ finalFileName);

        JSONObject json = new JSONObject(data);
        return json.toString();
    }
}
