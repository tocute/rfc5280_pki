<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome to Spring Web MVC project</title>
    </head>

    <body>
        <p>Hello! This is the default welcome page for a Spring Web MVC project.</p>
        <P>  The time on the server is ${serverTime}. </P>
        
        <form action="best">
            Account:<br>
            <input type="text" name="account" value="Mickey">
            <br>
            Password:<br>
            <input type="text" name="password" value="Mouse">
            <br><br>
            <input type="submit" value="Submit">
        </form>
    </body>
</html>
