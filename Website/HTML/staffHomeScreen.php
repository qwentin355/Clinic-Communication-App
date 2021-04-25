<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" ></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<?php session_start();
//if ($_SERVER["REQUEST_METHOD"] == "POST") {
//  if (isset($_POST['CookieVar'])) {
//    $_SESSION['PatientID'] = $_POST['CookieVar'];
//}
//}
?>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title> Staff Home Screen</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Josefin+Sans:wght@600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../CSS/main.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="../CSS/staffHomeScreen.css">

    <link rel="stylesheet" type="text/css" href="../CSS/HeaderStyle.css">

    <script src="../Scripts/Network.js"></script>


    <!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css">
    <nav>
        <div class="logo"> <h1 class="animate__animated animate__heartBeat"> staff home screen</h1></div>
    </nav>
    -->
</head>


<header>
    <div class="main_h pb-1">
        <div class="container-fluid">
            <div class="row justify-content-center align-items-center px-5">
                <div class="div1 col-sm-8 col-9"><img src="../Images/logo1.png" class="mr-2"><b>Staff Screen</b></div>
                <div class="div2 col-sm-4 col-3">
                    <!--<a href="index.php">Home</a>-->
                    <a href="logout.php">Log Out </a>
                </div>
            </div>
        </div>
    </div>
</header>

<body class="page_two">
<!--Header Navbar-->

<div class="container-fluid justify-content-center">
    <div class="scroll row pt-5 justify-content-center">
        <table id="Results" class="tableFixHead table col-lg-6"></table>
    </div>

    <section class="mt-4 mb-4">
        <div class="row justify-content-center align-items-center">
            <div class="col-md-10">
                <div class="row">
                    <div class="col-md-4"><button class="btn_grey mb-3" onclick="document.location.href='import_appointments.php'">Import<br> Appointment</button></div>
                    <div class="col-md-4"><button class="btn_grey mb-3" onclick="document.location.href='uploadWaitingRoomPicture.php'">Update<br> WaitingRooom</button></div>
                    <div class="col-md-4"> <button class="btn_search border-0 mb-3"  onclick="document.location.href='SearchScreen.php'"><img src="../Images/search.png" class="mb-2">Search</button></div>
                </div>
            </div>
        </div>
    </section>
</div>

</div>
</body>

</html>

<script>
    $(document).ready(function () {
        //$('#Results').empty();

        var formdata = new FormData();

        formdata.append("sessionID", "<?php echo $_COOKIE['sessionID']?>");
        formdata.append("username", "<?php echo $_SESSION['UserData']['Username']?>");


        postData("http://" + GetIP() + ":7000/search", formdata)
            .then(data => {
                if (JSON.parse(data).length != 0) {
                    $('#Results').append("<tr><th>Healthcard number</th><th>Patient Name</th><th>Appointment Time</th></tr>");
                    JSON.parse(data).forEach(function (item) {
                        $('#Results').append( "<tr><td>" + item['healthCardNum'] + "</td><td>" + item['patientName'] + "</td><td>"  +  new Date(item['appointmentTime'] * 1000) + "</td></tr>");
                    });
                } else {
                    alert("No appointments found");
                }
            }).catch(error => console.log(error.message));//Should catch for no API and No Internet;
    });

    async function postData(url = '', data) {
        // Default options are marked with *
        const response = await fetch(url, {
            method: 'POST',
            redirect: 'follow', // manual, *follow, error
            body: data // body data type must match "Content-Type" header
        });
        console.log(response);
        return response.json(); // parses JSON response into native JavaScript objects}

    }

</script>




