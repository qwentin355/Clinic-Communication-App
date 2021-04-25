"use strict"

function GetIP()
{
    var ip = location.host;
    if (ip.includes(":"))
    {
        ip = ip.substr(0, ip.lastIndexOf(":"));
    }
    console.log(ip);
    return ip;
}