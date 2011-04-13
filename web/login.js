function gotoLogin()
{
    //alert('goto login from ' + window.location.href);
    if(window.location.href.indexOf('localhost') == -1){
        window.location.href = "/login";
    }else{
        window.location.href = "http://localhost:4020/signup";
    }
}
