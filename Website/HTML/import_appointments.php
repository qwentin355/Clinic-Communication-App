

<!DOCTYPE html>
<html dir="ltr" lang="en-US">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="../CSS/bootstrap.min.css" type="text/css" />
    <!-- <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous"> -->
    <link rel="stylesheet" href="../CSS/main.css" type="text/css" />
    <link rel="stylesheet" href="../CSS/HeaderStyle.css" type="text/css" />
    <link rel="stylesheet" href="../CSS/ImportStyle.css" type="text/css" />
    <title>Import Appointments</title>
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


<body class="page_two">
<br>
<div class="container-fluid justify-content-center align-items-center row py-5">
    <div class="text-center Table col-lg-6 py-5">
        <form onsubmit="return false" enctype="text/plain" method="post">
            <input class="button" type="file" id="myFile" name="myFile" accept="text/csv">
            <input class="button" type="submit" name="submit" onclick="uploadFile();">
        </form>
    </div>
</div>
</body>
</html>

<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" ></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
<script src="../node_modules/axios/dist/axios.min.js"></script>
<script src="../Scripts/Network.js"></script>
<script>
    const input = document.getElementById('myFile');
    const curFiles = input.files;
    function uploadFile() {
        let formElement = document.querySelector('form');
        let formData = new FormData(formElement);
        formData.append("fileName", input.files[0].name);
        formData.append("myFile", input.files[0]);
        axios.post("http://" + GetIP() + ":7000/uploadApp", formData).then((response) => {
            console.log(response);
        }, (error) => {
            console.log(error);
        });

        return false;
    }
</script>

