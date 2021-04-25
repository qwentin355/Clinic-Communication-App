"use strict"
let interval;
let Message = null;
var formdata = new FormData();
window.onload = () => {
    document.getElementById("MessageError").innerHTML = " ";
}

/*
changeFunc - Attempts to set a cookie storing the patient number to be used by the messages page to load messages.
Doesn't seem to work.
*/
function changeFunc($i) {
    axios({
        method: 'POST',
        url: 'SearchScreen.php',
        data: {
            CookieVar: $i
        }
    });

    window.location.href = 'MessagesHTML.php' //Navigate to Message Page
}

//First Level Prevention - This code attempts to correct the Users inputs to
//prevent them from inputing any non allowed characters into the search fields

$("#MessageBox").keyup(function () {
    this.value = this.value.replace(/[^a-z ,^0-9,.'-+$\.]/g, '');
});

var messages = []; //Used in GetMessages to check if any new data has been received
/*
This function queries the API and if the backend returns an array greater than what is already stored
this function will store the result into the messages array.
 */
function GetMessages() {
    var healthnum=$("#healthnum").val();
    var user=$("#user_id").val();
    var sessionid=$("#session_id").val();
    formdata = new FormData();                                              //Purge old form data

    formdata.append("sessionID", sessionid);                          //create post body
    formdata.append("username", user);
    formdata.append("cardNum", healthnum);

    postData("http://" + GetIP() + ":7000/getMessages", formdata)             //Send post to getMessages
        .then(data => {
            if(JSON.parse(data).length != 0)                                //if we receive messages
            {
                if(JSON.parse(data).length > messages.length)               //and we recieve more messages than we are currently displaying
                {
                    let oldLength = messages.length;                        //store the number of messages we are displaying
                    messages = JSON.parse(data);                            //store the recived messages
                    displayMessages(oldLength)                              //Update the displayed messages
                }
            }
            else
            {
                alert("No Message History found");
            }
        }).catch(error => alert(error.message));                            //Should catch for no API and No Internet;
}

/*
This function displays the new messages received from the backend.
@Param - oldLen - this is expecting the number of messages currently being displayed so that we can skip to the new messages.
 */
function displayMessages(oldLen)
{
    for(let i = oldLen; i<messages.length; i++) //for each of the new messages append the message element to the message list.
    {
        if(messages[i]['staff']) {
            $('#msg_history').append("<div class='outgoing_msg'> <div class='sent_msg'> <p>" +
                messages[i]['message'] + "</p> <span class='time_date'> " + new Date(messages[i]['sentTime'] * 1000).toLocaleString() + "</span> </div> </div>");
        }
        else{
            $('#msg_history').append("<div class='incoming_msg'> <div class='received_msg'> <div class='received_withd_msg'> <p>" +
                messages[i]['message'] + " </p> <span class='time_date'>" + new Date(messages[i]['sentTime'] * 1000).toLocaleString() + "</span></div> </div> </div>");
        }
    }
}

/*
This function facilitates our post it makes a post to the url with the data as the body
@Param - url - The url that the post will be directed to
@Param - data - The body string or formdata which will be sent in the posts body.
#Resource - https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
*/
async function postData(url = '', data) {
    // Default options are marked with *
    const response = await fetch(url, {
        method: 'POST',
        redirect: 'follow',         // manual, *follow, error
        body: data                  // body data type must match "Content-Type" header
    });
    return response.json();         // parses JSON response into native JavaScript objects}

}

//Checks if values being sent are valid inputs
function isValid() {
    var e = document.getElementById("inputGroupSelect01");
    let bValid = true;
    formdata = new FormData();
    //Validate Message
    if (validateMessage()) {
        Message = document.getElementById('MessageBox').value;
        formdata.append("MessageBox", Message);
    } else {
        bValid = false;
    }
    //If all entries return as valid return true.
    return bValid;
}

/*Validate Message function
 */
function validateMessage() {
    let ComparatorMessage = ""; //clear comparator
    ComparatorMessage = document.getElementById('MessageBox').value; //get current value
    if (ComparatorMessage.toString().length==0 ) // if MessageBox is empty
    {
        document.getElementById("MessageError").innerHTML = '<p id="MessageError" class ="form-text text-muted error">Enter a message</p>';
        return false;
    }
    document.getElementById("MessageError").innerHTML = '<p></p>';

    return true;
}

//Sets a Cookie using the incoming parameteres as values
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires;
}

//Checks if the value contains any Special Characters
function containsSpecial(value) {
    return /.*[`!@#$%^&*()_+\-=\[\]{};"\\|<>\~].*/.test(value);
}
$(document).on('page:beforeout', function (e){clearInterval(interval);});
