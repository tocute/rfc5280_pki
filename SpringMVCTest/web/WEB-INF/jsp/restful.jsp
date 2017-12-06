<%-- 
    Document   : restful
    Created on : 2017/12/5, 上午 11:14:40
    Author     : bill.chang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
        <script src="../js/restful.js"></script>
    </head>
    <body>
        <h1>Restful + jQuery</h1>
        ID :
        <input type="text" value=0123 id="text_id">
        <br>
        <button type="button" id="btn_get"> 
                Get
        </button>
        <button type="button" id="btn_post"> 
                Post
        </button>
        <button type="button" id="btn_put"> 
                Put
        </button>
        <button type="button" id="btn_delete"> 
                Delete
        </button>
        <br>

        <h1>Upload File</h1>
        
        <div id="uploadForm">
            <input id="file" type="file"/>
            <button id="btn_upload" type="button">upload</button>
        </div>
        <br>
        <p id="rsp_msg">TEST</p>
        <div id="div_img"/>

        
    </body>
</html>
