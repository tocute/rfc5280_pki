/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){ 
    var onBtnGet = function (e)
    {
        var user_id = $("#text_id").val();
        $.get( "/WebApplication1/restful/user/"+user_id, 
        function( data, status ) {
          if(data["ReturnCode"] == 0)
          {
            var success_msg = $("#rsp_msg");
            success_msg.text("成功 "+data.Message);
          }
          else
          {
            var fail_msg = $("#rsp_msg");
            fail_msg.text("失敗 "+data.Message);
          } 
        });
    };
    
    var onBtnPost = function (e)
    {
        var user_id = $("#text_id").val();
        $.post( "/WebApplication1/restful/user/"+user_id, 
            {},
            function( data, status ) 
            {
                if(data["ReturnCode"] == 0)
                {
                  var success_msg = $("#rsp_msg");
                  success_msg.text("成功 "+data.Message);
                }
                else
                {
                  var fail_msg = $("#rsp_msg");
                  fail_msg.text("失敗 "+data.Message);
                } 
            });
    };
    
    var onBtnPut = function (e)
    {
        var user_id = $("#text_id").val();
        $.ajax({
            url: '/WebApplication1/restful/user/'+user_id,
            type: 'PUT',
            success: function(data) {
                if(data["ReturnCode"] == 0)
                {
                    var success_msg = $("#rsp_msg");
                    success_msg.text("成功 "+data.Message);
                }
                else
                {
                    var fail_msg = $("#rsp_msg");
                    fail_msg.text("失敗 "+data.Message);
                } 
            }
        });
    };
    
    var onBtnDelete = function (e)
    {
        var user_id = $("#text_id").val();
        $.ajax({
            url: '/WebApplication1/restful/user/'+user_id,
            type: 'DELETE',
            success: function(data) {
                if(data["ReturnCode"] == 0)
                {
                    var success_msg = $("#rsp_msg");
                    success_msg.text("成功 "+data.Message);
                }
                else
                {
                    var fail_msg = $("#rsp_msg");
                    fail_msg.text("失敗 "+data.Message);
                } 
            }
        });
    };
    
     
    var onBtnUploadFile = function(e)
    {
        //console.log("onBtnUploadFile 1");
        var formData = new FormData();
        formData.append('file', $('#file')[0].files[0]);
        
        $.ajax({
            url: '/WebApplication1/restful/upload',
            type: 'POST',
            cache: false,
            data: formData,
            processData: false,
            contentType: false
        }).done(function(data) {
//            var obj = jQuery.parseJSON( data );
//                alert(obj.a);
//                alert(obj.b);
            $('#div_img').prepend('<img id="div_img" src="'+ data.URL +'" width=200/>');
            var success_msg = $("#rsp_msg");
            success_msg.text("上傳成功 ");
        }).fail(function(data) {
            var fail_msg = $("#rsp_msg");
            fail_msg.text("上傳失敗 ");
        });
    };
    
    $("#btn_get").on("click", onBtnGet);
    $("#btn_post").on("click", onBtnPost);
    $("#btn_put").on("click", onBtnPut);
    $("#btn_delete").on("click", onBtnDelete);
    $("#btn_upload").on("click", onBtnUploadFile);
});
