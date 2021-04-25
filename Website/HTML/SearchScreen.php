<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>

<?php
session_start(); /* Starts the session */
$Name = "";
$Doctor = "";
$Date = "";
$Health = "";
$Criteria = "";

$TextError = "";
$DateError = "";
$DoctorError = "";
$HealthCardError = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['CookieVar'])) {
        $_SESSION['PatientID'] = $_POST['CookieVar'];
    }
}
?>



<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Search Appointments</title>
    <meta name="description" content="Search for Guest">
    <meta name="author" content="Quentin">
    <!-- Latest compiled and minified CSS -->
    <!--    <link rel="stylesheet" href="../CSS/bootstrap.min.css" type="text/css"/>-->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <!-- jQuery lirary -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <!-- Popper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>

    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

    <link rel="stylesheet" href="../CSS/main.css" type="text/css"/>
    <link rel="stylesheet" href="../CSS/HeaderStyle.css" type="text/css"/>

    <script src="../Scripts/SearchFormValidation.js"></script>
    <script src="../Scripts/Network.js"></script>


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

<body class="page_two">

<div class="container-fluid h-100">
    <div  class="row pt-5 justify-content-center">
        <div class="searchBox col-lg-5  justify-content-center border">
            <h3 class="pb-4 pt-5 pl-4">Search Options</h3>

            <form method="post" class=" justify-content-center" action="#">
                <div class="mb-0 input-group input-group-lg pl-4">
                    <!--                    <label for="Patient">Patient Name</label>-->
                    <!--                    <span class="input-group-addon">Patient</span>-->
                    <input type="Text" name="Patient" class="form-control pb-2" id="Patient"
                           placeholder="Enter Patient Name">
                    <input type="hidden" name="user_id" value="<?php echo $_SESSION['UserData']['Username']; ?>" class="form-control pb-2" id="user_id">
                    <?php
                    $my_array = $_COOKIE ;
                    ?>
                    <input type="hidden" name="session_id" value="<?php  echo $my_array["sessionID"]; ?> " class="form-control pb-2" id="session_id"
                           placeholder="Enter Patient Name">
                </div>
                <div class=" pl-4">
                    <small id="patientNameError" class="form-text text-muted">Update Later</small>
                </div>

                <div class="mt-4 input-group input-group-lg pl-4">
                        <select class="custom-select form-control pb-2" id="inputGroupSelect01">
                            <option value = "" selected>Select the Doctor Name</option>
                            <option value="Dr. Strange">Dr. Strange</option>
                            <option value="Dr. Who">Dr. Who</option>
                            <option value="Dr. Mario">Dr. Mario</option>
                            <option value="Dr. Brown">Dr. Brown</option>
                        </select>
                </div>
                <div class=" pl-4">
                    <small id="doctorNameError" class="form-text text-muted">Update Later</small>
                </div>
                <div class="mt-4 input-group input-group-lg pl-4">
                    <!--                    <input class="form-check-input " name='Criteria' type="text" value="Date">-->
                    <!--                    <span class="input-group-addon">Date</span>-->
                    <input type="date" name="Date" class="form-control" id="Date" placeholder=" Select Date Here">
                </div>
                <div class=" pl-4">
                    <small id="dateNameError" class="form-text text-muted">Update Later</small>
                </div>
                <div class="mt-4 input-group input-group-lg pl-4 ">
                    <!--                    <input class="form-check-input " name='Criteria' type="text" value="HealthCard">-->
                    <!--                    <span class="input-group-addon">HealthCard</span>-->
                    <input type="text" name="Health" class="form-control" id="Health"
                           placeholder="Enter Health Card Number">
                </div>
                <div class=" pl-4">
                    <small id="HCardError" class="form-text text-muted">Update Later</small>
                </div>
                <div class=text-center>
                    <input id="Search" class="mb-4 mt-4 pl-5 pr-5 btn btn-secondary" type="submit"></input>
                </div>
            </form>
        </div>
        <!--
        Results of searchcalling this done Still needs testing
        -->
        <div class="resultBox ml-2 col-lg-6 border ">
            <h3 class="pt-5 pl-4 pb-4">Results</h3>
            <div class="resultCont form-group ">

                <select multiple class="form-control h-75" id="Results" onchange="changeFunc(value);"> <!--select-->
                    <!--Options-->
                    <option>

                    </option>
                    <option>

                    </option>
                </select>
            </div>
        </div>


    </div>
</div>
</body>
<footer>

</footer>
</html>
<script>
    /*
Search - Submits a get request to the server expects an array of json objects containing patent info.
*/
    $(document).ready(function () {
        $('#Search').click(function (e) {

            e.preventDefault();

            if (isValid() == true)//After this line is XMLHttpRequest Acceptance test criteria
            {
                $('#Results').empty();

                formdata.append("sessionID", "<?php echo $_COOKIE['sessionID']?>");
                formdata.append("username", "<?php echo $_SESSION['UserData']['Username']?>");

                postData("http://" + GetIP() + ":7000/search", formdata)
                    .then(data => {
                        if (JSON.parse(data).length != 0) {
                            JSON.parse(data).forEach(function (item) {
                                $('#Results').append("<option value=" + item['healthCardNum'] + ">" + 'Name: ' + item['patientName'] + '  Appointment Time: ' + new Date(item['appointmentTime'] * 1000) + "</option>");
                            });
                        } else {
                            alert("No appointments found");
                        }

                    }).catch(error => alert(error.message));//Should catch for no API and No Internet;
            }
        });
    });
</script>
