"use strict"

let Doctor = null;
let Health = null;
let DateVar = null;
let Patient = null;
var formdata = new FormData();
window.onload = ()=>{
    //document.getElementById("patientNameError").innerHTML = '<small id="HCardError" class ="form-text text-muted error">Update Later</small>';
    document.getElementById("patientNameError").innerHTML = "Enter First and Last Name of Patient";
    document.getElementById("doctorNameError").innerText = "Enter First and Last Name of Doctor";
    document.getElementById("dateNameError").innerText = "Select the date of appointment";
    document.getElementById("HCardError").innerText = "Enter 9 digit healthcard number";
}

/*
changeFunc - Attempts to set a cookie storing the patient number to be used by the messages page to load messages.
Doesn't seem to work.
*/
function changeFunc($i) {
    window.location.href = "MessagesHTML.php?healthnum=" + $i + " " //Navigate to Message Page
}

//First Level Prevention - This code attempts to correct the Users inputs to prevent them from inputing any non allowed characters into the search fields
   $("#Health").keyup(function(){
this.value = this.value.replace(/[^0-9\.]/g, '');
});
$("#Patient").keyup(function(){
this.value = this.value.replace(/[^a-z ,.'-+$\.]/g, '');
});
$("#Doctor").keyup(function(){
this.value = this.value.replace(/[^a-z ,.'-+$\.]/g, '');
});

/*
Search - Submits a get request to the server expects an array of json objects containing patent info.
*/
$(document).ready(function () {
    $('#Search').click(function (e) {
        var user = $("#user_id").val();
        var sessionid = $("#session_id").val();
        e.preventDefault();

        if (isValid() === true)//After this line is XMLHttpRequest Acceptance test criteria
        {
            $('#Results').empty();

            formdata.append("sessionID", sessionid);
            formdata.append("username", user);
        }
    });
});


function isJSON(Str)
{
      if (/^[\],:{}\s]*$/.test(Str.replace(/\\["\\\/bfnrtu]/g, '@').
                    replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
                    replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

                     return true

                    }
      return false
}

// Example POST method implementation:
async function postData(url = '', data) {
    // Default options are marked with *
    const response = await fetch(url, {
        method: 'POST',
        redirect: 'follow', // manual, *follow, error
        body: data // body data type must match "Content-Type" header
    });
    return response.json(); // parses JSON response into native JavaScript objects}

}
//Checks if values being sent are valid inputs
function isValid() {
    var e = document.getElementById("inputGroupSelect01");

    let bValid = true;
    formdata = new FormData();
    //Validate Healthcard
    if(document.getElementById('Health').value !== "") {
        if (validateHealthcard()) {
            Health = document.getElementById('Health').value;
            formdata.append("HealthCard", Health);
        } else {
            bValid = false;
        }
    }
    //Validate Doctor
    if(e.value !== "") {
            Doctor = e.value;
            formdata.append("Doctor", Doctor);
        }

    //Validate Patient
    if(document.getElementById('Patient').value !== "") {
        if (validatePatientName()) {
            Patient = document.getElementById('Patient').value;
            formdata.append("Patient", Patient);
        } else {
            bValid = false;
        }
    }

    valiDate();

    //If all entries return as valid return true.
    return bValid;
}
function valiDate()
{
    let ComparatorDate = "";
    ComparatorDate = document.getElementById("Date").value;
    if(ComparatorDate === "")
    {
       return;
    }
    else
    {
        DateVar = (new Date(document.getElementById("Date").value).getTime() / 1000).toString();
    }
    formdata.append("Date", DateVar);
}
/*Validate Health card function
Summary - This function validates that the value in the document.getElementByID('Health').value is
 1: not empty and no greater than 9 characters
 2: not empty and no less than 9 characters
 3: not empty and is numeric
 4: not empty and does not contain special characters
 if all of these criteria are met the value is considered valid.
 */
function validateHealthcard() {
    let ComparatorHealthcard = ""; //clear comparator

    ComparatorHealthcard = document.getElementById('Health').value; //get current value

    if (ComparatorHealthcard.length !== 0 && containsSpecial(ComparatorHealthcard)) //if healthcard is not empty and contains special characters
    {
        document.getElementById("HCardError").innerHTML = '<p id="HCardError" class ="form-text text-muted error">Health Card cannot contain special characters</p>';
        return false;
    }
    if (ComparatorHealthcard.length !== 0 && !isNumeric(ComparatorHealthcard)) // if healthcard not empty and is not numeric
    {
        document.getElementById("HCardError").innerHTML = '<p id="HCardError" class ="form-text text-muted error">Health card must be numeric</p>';
        return false;
    }
    if (ComparatorHealthcard.length !== 0 && ComparatorHealthcard.length > 9) // if healthcard is not empty and too long
    {

        document.getElementById("HCardError").innerHTML = '<p id="HCardError" class ="form-text text-muted error">Health Card can be no greater than 9 characters</p>';
        return false;
    }
    if (ComparatorHealthcard.length !== 0 && ComparatorHealthcard.length < 9) // if healthcard is not empty and too short
    {
        document.getElementById("HCardError").innerHTML = '<p id="HCardError" class ="form-text text-muted error">Health Card can be no less than 9 characters</p>';
        return false;
    }
    //resets the helth card error and returns true if valid or empty
    document.getElementById("HCardError").innerText = "Enter 9 digit healthcard number";
    return true;
}

//Validate PatientName function
function validatePatientName() {
    let ComparatorPatientName = ""; //clear comparator
    ComparatorPatientName = document.getElementById('Patient').value; //get current value

    if (ComparatorPatientName.length > 50) { //if patient name is greater than 50
        document.getElementById("patientNameError").innerHTML = '<small id="patientNameError" class ="form-text text-muted error">Name cannot contain more than 50 characters.</small>';
        return false;
    }
    if (ComparatorPatientName.length !== 0 && containsSpecial(ComparatorPatientName)) // if patient name is not empty and contains any special characters
    {
        document.getElementById("patientNameError").innerHTML = '<small id="patientNameError" class ="form-text text-muted error">Name cannot contain special characters.</small>';
        return false;
    }
    if (ComparatorPatientName.length !== 0 && containsNumbers(ComparatorPatientName)) // if patient name is not empty and contains any Numbers
    {
        document.getElementById("patientNameError").innerHTML = '<small id="patientNameError" class ="form-text text-muted error">Name cannot contain any numbers.</small>';
        return false;
    }
    if (ComparatorPatientName.length !== 0 && !isName(ComparatorPatientName)) // if patient name is not empty and is not in the FirstName LastName Format
    {
        document.getElementById("patientNameError").innerHTML = '<small id="patientNameError" class ="form-text text-muted error">Name needs to be in "FirstName LastName" format.</small>';
        return false;
    }
    document.getElementById("patientNameError").innerHTML = "Enter First and Last Name of Patient";
    return true;

}

//Validate Doctor Function
function validateDoctorName(e) {
    let ComparatorDoctorName = ""; //clear comparator
    ComparatorDoctorName = e.value; //get current value

    if (ComparatorDoctorName.length > 50) { //if doctor name is greater than 50
        document.getElementById("doctorNameError").innerHTML = '<small id="doctorNameError" class ="form-text text-muted error">Name cannot contain more than 50 characters.</small>';
        return false;
    }
    if (ComparatorDoctorName.length !== 0 && containsSpecial(ComparatorDoctorName)) // if doctor name is not empty and contains any special characters
    {
        document.getElementById("doctorNameError").innerHTML = '<small id="doctorNameError" class ="form-text text-muted error">Name cannot contain special characters.</small>';
        return false;
    }
    if (ComparatorDoctorName.length !== 0 && containsNumbers(ComparatorDoctorName)) // if doctor name is not empty and contains any Numbers
    {
        document.getElementById("doctorNameError").innerHTML = '<small id="doctorNameError" class ="form-text text-muted error">Name cannot contain any numbers.</small>';
        return false;
    }
    if (ComparatorDoctorName.length !== 0 && !isName(ComparatorDoctorName)) // if doctor name is not empty and is not in the FirstName LastName Format
    {
        document.getElementById("doctorNameError").innerHTML = '<small id="doctorNameError" class ="form-text text-muted error">Name needs to be in "FirstName LastName" format.</small>';
        return false;
    }
    document.getElementById("doctorNameError").innerText = "Enter First and Last Name of Doctor";
    return true;
}

/*Displays an error message as an alert
Summary - Displays the error message passed to it
param  - errorMessage - contains the message intended to be shown to the user
 */
function errorAlert(errorMessage) {
}

//Sets a Cookie using the incoming parameteres as values
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires;
}

//Checks if the value is a name with FirstName LastName Format
function isName(value) {
    return /^[a-zA-Z]+ [a-zA-Z]+$/i.test(value);
}

//Checks if the value is a number
function isNumeric(value) {
    return /^\d+$/.test(value);
}

//Checks if the value contains any Special Characters
function containsSpecial(value) {
    return /.*[`!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~].*/.test(value);
}

//Checks if the value contains Numbers
function containsNumbers(value){
    return /.*[0-9].*/.test(value);
}