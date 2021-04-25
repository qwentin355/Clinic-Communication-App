
<script src="../node_modules/axios/dist/axios.min.js"></script>
<script src="../Scripts/Network.js"></script>

<?php
session_start(); /* Starts the session */

if(isset($_POST['Submit'])){
    $Username = isset($_POST['Username']) ? $_POST['Username'] : '';
    $Password = isset($_POST['Password']) ? $_POST['Password'] : '';

    if(isset($_COOKIE['status'])) {
        $status = $_COOKIE['status'];
        setcookie("status", "", time() - 3600);
        if($status == '200') {
            $user = $_COOKIE['uname'];
            $session = $_COOKIE['sessionID'];
            //setcookie("uname", "", time() - 3600);
            /*Success: Set session variables and redirect to Protected page  */
            $_SESSION['UserData']['Username']=$user;
            $_SESSION['UserData']['SessionId']=$session;
            $_SESSION['failed_attempts']= 0;
            header("location:staffHomeScreen.php");
            exit;
        }

        else{
            /*Unsuccessful attempt: Set error message*/
            setcookie("uname", "", time() - 3600);
            $msg="<span style='color:red'>Invalid Login Details</span>";
            $_SESSION['failed_attempts'] ++;
          
        }
        if(isset($_SESSION['locked']))
        {

            $diff = time() - $_SESSION['locked'];
            if($diff > 10)
            {
                unset($_SESSION['locked']);
                unset($_SESSION['failed_attempts']);
            }
        }
    }

    else
    {

        echo "
            <script>
                let user = '$Username';
                let pass = '$Password';
                document.cookie = 'uname=' + user;
                let formData = new FormData();
                formData.append('username', user);
                formData.append('password', pass);
                axios({
                    method: 'post',
                    url: 'http://' + GetIP() + ':7000/login',
                    data: formData,
                    headers: { 'Content-Type': 'multipart/form-data' }
                }).then(function (response) {
                    //handle success
                    
                    let rs = response.status;   
                    if(rs == 200){
                        document.cookie = 'status=200';
                        document.cookie = 'sessionID =' + response.data;
                    }
                    else{
                        document.cookie = 'status=401';
                    }
                    console.log(response);
                    location.reload();
                }).catch(function (response) {
                    document.cookie = 'status=401';
                    console.log(response);
                    location.reload();
                });
            </script>
        ";
    }
}


?>
<!doctype html>

<html>
<head>
    <meta charset="utf-8">
    <title>PHP Login Script</title>

    <link href="../CSS/style.css" rel="stylesheet">
    <link href="../CSS/loginCSS.css" rel="stylesheet">
    <link rel="stylesheet" href="../CSS/main.css" type="text/css"/>
    <link rel="stylesheet" href="../CSS/bootstrap.min.css" type="text/css" />
</head>

<header class="text-center pt-5">
    <div class="main_h pb-1 col-lg-6 ">
        <div class="container-fluid col-lg-11  ">
            <div class="row align-items-center">
                <div class="div1 col-sm-1"><img src="../Images/logo1.png"></div>
                <div class="col-sm-6"></div>
                <div class="div2 col-sm-5">
                    <b>Staff Login Form</b>
                </div>
            </div>
        </div>
    </div>
</header>

<body>
<br>
<div class="container-fluid col-lg-5">

<form action="" method="post" name="Login_Form">
    <Table width="400" border="0" align="center" cellpadding="10" cellspacing="1" class="Table"></Table>
<br>

<form action="" method="post" name="Login_Form">
    <table width="400" border="0" align="center" cellpadding="5" cellspacing="1" class="Table">

        <?php if(isset($msg)){?>
            <tr>
                <td colspan="2" align="center" valign="top"><?php echo $msg;?></td>
            </tr>
        <?php } ?>
        <tr>
            <td colspan="2" align="left" valign="top"><h3>Login</h3></td>
        </tr>
        <tr>
            <td align="right" valign="top">Username</td>
            <td><input name="Username" type="text" class="Input"></td>
        </tr>
        <tr>
            <td align="right">Password</td>
            <td><input name="Password" type="password" class="Input"></td>
        </tr>
        <tr>


            <td>&nbsp;
                <?php
                if(isset($_SESSION['failed_attempts']) && $_SESSION['failed_attempts']> 2)
                {
                    $_SESSION['locked'] = time();
                   ?>
                <script>
                    interval = setTimeout(function()
                    {                                //Sets a timer which polls the backend for new messages
                        <?php
                        $_SESSION['failed_attempts'] = 0;
                        unset($_SESSION['locked']);
                        ?>
                        location.reload();
                           }, 10000);

                </script>
                <?php
                    echo "<p style='color: red'><b>Please wait for 10 seconds</b> </p>";
                }
                else {
                ?>
            <td>

                <input name="Submit" type="submit" value="Login" class="Button3">
            </td>

            <?php } ?>
            </td>

        </tr>
    </table>
</form>
</div>
</body>

</html>

