<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
<script src="../Scripts/Network.js"></script>
<?php
session_start(); /* Starts the session */

?>

<html>
<head>
    <meta charset="utf-8">

    <title>Messages</title>
    <meta name="description" content="Search for Guest">
    <meta name="author" content="Quentin">
    <!-- Latest compiled JavaScript -->


    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <!-- jQuery lirary -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="../CSS/main.css" type="text/css"/>
    <script src="../Scripts/ChatFormValidation.js"></script>
    <link rel="stylesheet" href="../CSS/MessagesCSS.css" type="text/css"/>
    <link rel="stylesheet" href="../CSS/HeaderStyle.css" type="text/css"/>

    <script src="../Scripts/Network.js"></script>


</head>
<header>
    <div class="main_h pb-1">
        <div class="container-fluid">
            <div class="row justify-content-center align-items-center px-5">
                <div class="div1 col-sm-8 col-9"><img src="../Images/logo1.png" class="mr-2"><b>Staff Screen</b></div>
                <div class="div2 col-sm-4 col-3">
                    <a href="staffHomeScreen.php">Home</a>
                    <a href="logout.php">Log Out </a>
                </div>
            </div>
        </div>
    </div>
</header>



<body>
<div class="container mb-4">

</div>
<div class="container-fluid h-100 mh-100">
    <div class="row justify-content-center h-90">

        <div class="resultBox ml-4 mr-4 mb-4 col-lg border ">
            <!--Row 1-->
            <div class="headers row border">
                <input type="hidden" id="healthnum" value="<?php echo $_GET['healthnum']; ?>"/>
                <input type="hidden" name="user_id" value="<?php echo $_SESSION['UserData']['Username'];?>" class="form-control pb-2" id="user_id">
                <?php
                $my_array = $_COOKIE ;
                ?>
                <input type="hidden" name="session_id" value="<?php  echo $my_array["sessionID"]; ?> " class="form-control pb-2" id="session_id"
                       placeholder="Enter Patient Name">
                <h3 id="patient"></h3>
            </div>
            <!--Row 2-->
            <div class="row justify-content-center overflow-auto h-100 mh-80 border">
                <div class="mesgs w-80 mh-80 ">
                    <div id="msg_history" class="msg_history">
                       <!--Message history will be populated here-->
                    </div>
                </div>
            </div>
            <!--Row 3-->
            <div class="row">
                <div class="input-group ">
                    <input type="text" id="MessageBox" class="form-control" placeholder="Type a message"/>

                    <div class="input-group-append">

                        <button id="SubmitMessage" class="bttn btn-outline-secondary" >Send</button>
                    </div>
                </div>
            </div>
            <div class="row">
            <div class="pl-4">
                <small id="MessageError" class="form-text text-muted"></small>
            </div>
            </div>
        </div>


        <div class="resultBox mr-4 ml-4 mb-4 col-lg border">
            <!--Row 1-->
            <div class="headers row border">
                <h3>Appointment Details</h3>

            </div>
            <!--Row 2-->
            <div class="row justify-content-center overflow-auto h-90 mh-90 border">
                <!-- where we have to show details-->
                <table bgcolor="#f2f2f2" height="100%" width="100%">
                    <tr>
                        <th style="padding-left: 100px" height="70px">Patient Name :</th>
                        <td id="pName"></td>
                    </tr>
                    <tr>
                        <th style="padding-left: 100px" height="70px">Doctor Name :</th>
                        <td id="dName"></td>
                    </tr>
                    <tr>
                        <th style="padding-left: 100px" height="70px">Appointment Date & Time:</th>
                        <td id="aptDate"></td>
                    </tr>

                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>

<script>
    var input = document.getElementById("MessageBox");
    input.addEventListener("keyup", function(event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            document.getElementById("SubmitMessage").click();
        }
    });
    /*
This Block of code does the initialization for the MessagesHTML page, it:
- populates the messages
- sets a timer for polling the  backend for new messages
- populates the appointment data
- sets the send buttons onClick event
*/
    $(document).ready(function () {
        var user = "<?php echo $_SESSION['UserData']['Username']?>";
        var session =  "<?php echo $_COOKIE['sessionID']?>";
        console.log("set: " +session + " " + user);
        interval = setInterval(function(){                                //Sets a timer which polls the backend for new messages
            GetMessages(user, session); }, 10000);

        GetMessages(user,session);                                                           //Initial GetMessages to populate the messages div.

        var health = document.getElementById("healthnum").value;        //Define patient data html elements
        var patient = document.getElementById("patient");
        var pName = document.getElementById("pName");
        var dName = document.getElementById("dName");
        var aptDate = document.getElementById("aptDate");

        formdata.append("HealthCard", health);                            //Prepair to search for patient data
        formdata.append("sessionID", session);
        formdata.append("username", user);

        postData("http://" + GetIP() + ":7000/search", formdata)                  //Search for Patient Data
            .then(data => {
                if (JSON.parse(data).length != 0) {                             //if data is found display patient data
                    JSON.parse(data).forEach(function (item) {
                        patient.innerText = item['patientName'];
                        pName.innerText = item['patientName'];
                        dName.innerText = item['doctorName'];
                        let d = new Date(item['appointmentTime'] * 1000);
                        aptDate.innerText =d.toLocaleString();
                    });
                } else {                                                        //alert if no data is found
                    alert("No Patient found");
                }

            }).catch(error => alert(error.message));                            //alert if no API and No Internet;

        //Set onClick for "Send" button
        $('#SubmitMessage').click(function (e) {
            formdata = new FormData();                                          //Purge old form data

            var message=$("#MessageBox").val();                                 //Get and store values to be sent
            var healthnum=$("#healthnum").val();
            var  DateVar = Math.floor(new Date().getTime() / 1000).toString();

            e.preventDefault();                                                 //prevent the page from refreshing

            if (isValid() == true) //After this line is XMLHttpRequest Acceptance test criteria
            {
                formdata.append("sessionID", session);                  //creates post body
                formdata.append("username", user);
                formdata.append("message", message);
                formdata.append("cardNum", healthnum);
                formdata.append("sentByStaff", "true");
                formdata.append("time", DateVar);

                postData("http://" + GetIP() + ":7000/newMessage", formdata)      //Sends the post an processes the responce
                    .then(response=>{
                        //handle success
                        console.log(response);
                        GetMessages(user, session);
                    }).catch(error => alert(error.message));                    //Should catch for no API and No Internet;
                $("#MessageBox").val('');                                       //clears the message text input
            }
        });
    });
</script>

